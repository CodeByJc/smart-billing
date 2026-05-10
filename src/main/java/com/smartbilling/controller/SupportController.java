package com.smartbilling.controller;

import com.smartbilling.dao.SupportPaymentDAO;
import com.smartbilling.model.SupportPayment;
import com.smartbilling.service.RazorpayService;
import com.smartbilling.service.RazorpayService.RazorpayOrderResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Public support page and Razorpay payment flow.
 */
@Controller
@RequestMapping("/support")
public class SupportController {

    private static final List<String> ALLOWED_SUPPORT_TYPES = Arrays.asList("GIFT", "DONATE");
    private static final List<BigDecimal> ALLOWED_AMOUNTS = Arrays.asList(
            new BigDecimal("100"),
            new BigDecimal("200"),
            new BigDecimal("500"),
            new BigDecimal("1000"));

    @Autowired
    private RazorpayService razorpayService;

    @Autowired
    private SupportPaymentDAO supportPaymentDAO;

    @GetMapping
    public String supportPage() {
        return "support";
    }

    @PostMapping("/create-order")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createOrder(@RequestParam String supportType,
            @RequestParam BigDecimal amount) {
        try {
            String normalizedType = normalizeSupportType(supportType);
            BigDecimal normalizedAmount = normalizeAmount(amount);

            RazorpayOrderResponse order = razorpayService.createOrder(normalizedAmount, normalizedType);

            SupportPayment payment = new SupportPayment();
            payment.setSupportType(normalizedType);
            payment.setAmount(normalizedAmount);
            payment.setRazorpayOrderId(order.getOrderId());
            payment.setPaymentStatus("PENDING");
            supportPaymentDAO.insert(payment);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("keyId", order.getKeyId());
            response.put("orderId", order.getOrderId());
            response.put("amount", order.getAmount().toPlainString());
            response.put("amountInPaise", order.getAmountInPaise());
            response.put("currency", order.getCurrency());
            response.put("supportType", normalizedType);
            response.put("receipt", order.getReceipt());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return errorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/verify")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verifyPayment(HttpServletRequest request,
            @RequestParam String razorpay_order_id,
            @RequestParam String razorpay_payment_id,
            @RequestParam String razorpay_signature) {
        try {
            SupportPayment payment = supportPaymentDAO.findByOrderId(razorpay_order_id);
            if (payment == null) {
                return errorResponse(HttpStatus.NOT_FOUND, "Support payment order not found.");
            }

            if (!razorpayService.verifySignature(razorpay_order_id, razorpay_payment_id, razorpay_signature)) {
                supportPaymentDAO.updateFailed(razorpay_order_id, razorpay_payment_id);
                return errorResponse(HttpStatus.BAD_REQUEST, "Payment signature verification failed.");
            }

            supportPaymentDAO.updateSuccess(razorpay_order_id, razorpay_payment_id);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("redirectUrl", buildUrl(request, "/support/thank-you?paymentId=" + razorpay_payment_id));
            response.put("paymentId", razorpay_payment_id);
            response.put("orderId", razorpay_order_id);
            response.put("amount", payment.getAmount().toPlainString());
            response.put("supportType", payment.getSupportType());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/payment-failed")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> paymentFailed(HttpServletRequest request,
            @RequestParam String razorpay_order_id,
            @RequestParam(required = false) String razorpay_payment_id,
            @RequestParam(required = false) String reason) {
        try {
            SupportPayment payment = supportPaymentDAO.findByOrderId(razorpay_order_id);
            if (payment != null) {
                supportPaymentDAO.updateFailed(razorpay_order_id, razorpay_payment_id);
            }

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("redirectUrl", buildUrl(request, "/support/payment-failed?orderId=" + razorpay_order_id));
            response.put("reason", reason);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("/thank-you")
    public String thankYou(@RequestParam(required = false) String paymentId,
            @RequestParam(required = false) String orderId,
            Model model) {
        SupportPayment payment = findPayment(paymentId, orderId);
        populatePaymentModel(model, payment, paymentId, orderId);
        return "thank-you";
    }

    @GetMapping("/payment-failed")
    public String paymentFailedPage(@RequestParam(required = false) String orderId,
            @RequestParam(required = false) String paymentId,
            Model model) {
        SupportPayment payment = findPayment(paymentId, orderId);
        populatePaymentModel(model, payment, paymentId, orderId);
        return "payment-failed";
    }

    private SupportPayment findPayment(String paymentId, String orderId) {
        if (paymentId != null && !paymentId.isBlank()) {
            SupportPayment payment = supportPaymentDAO.findByPaymentId(paymentId);
            if (payment != null) {
                return payment;
            }
        }
        if (orderId != null && !orderId.isBlank()) {
            return supportPaymentDAO.findByOrderId(orderId);
        }
        return null;
    }

    private void populatePaymentModel(Model model, SupportPayment payment, String paymentId, String orderId) {
        if (payment != null) {
            model.addAttribute("supportType", payment.getSupportType());
            model.addAttribute("amount", payment.getAmount());
            model.addAttribute("orderId", payment.getRazorpayOrderId());
            model.addAttribute("paymentId",
                    payment.getRazorpayPaymentId() != null ? payment.getRazorpayPaymentId() : paymentId);
            model.addAttribute("paymentStatus", payment.getPaymentStatus());
            model.addAttribute("createdAt", payment.getCreatedAt());
        } else {
            model.addAttribute("supportType", "");
            model.addAttribute("amount", "");
            model.addAttribute("orderId", orderId);
            model.addAttribute("paymentId", paymentId);
            model.addAttribute("paymentStatus", "");
        }
    }

    private String normalizeSupportType(String supportType) {
        if (supportType == null) {
            throw new IllegalArgumentException("Support type is required.");
        }
        String normalized = supportType.trim().toUpperCase();
        if (!ALLOWED_SUPPORT_TYPES.contains(normalized)) {
            throw new IllegalArgumentException("Invalid support type.");
        }
        return normalized;
    }

    private BigDecimal normalizeAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount is required.");
        }
        BigDecimal normalized = amount.setScale(0, java.math.RoundingMode.UNNECESSARY);
        for (BigDecimal allowedAmount : ALLOWED_AMOUNTS) {
            if (allowedAmount.compareTo(normalized) == 0) {
                return normalized;
            }
        }
        throw new IllegalArgumentException("Invalid amount selected.");
    }

    private ResponseEntity<Map<String, Object>> errorResponse(HttpStatus status, String message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }

    private String buildUrl(HttpServletRequest request, String path) {
        return request.getContextPath() + path;
    }
}