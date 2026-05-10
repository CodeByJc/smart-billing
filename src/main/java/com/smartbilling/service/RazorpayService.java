package com.smartbilling.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handles Razorpay order creation and signature verification.
 */
@Service
public class RazorpayService {

    private static final String KEY_ID = System.getProperty("razorpay.key", "rzp_test_SdQa2hiAwd54K0");
    private static final String KEY_SECRET = System.getProperty("razorpay.secret", "HhZ7D4sHCo9gTI6utgff1cB0");
    private static final String CURRENCY = "INR";
    private static final String BASE_URL = "https://api.razorpay.com/v1/orders";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    public RazorpayOrderResponse createOrder(BigDecimal amount, String supportType) {
        ensureConfigured();

        BigDecimal normalizedAmount = amount.setScale(2, RoundingMode.HALF_UP);
        long amountInPaise = normalizedAmount.multiply(BigDecimal.valueOf(100)).longValueExact();
        String receipt = "support-" + Instant.now().toEpochMilli();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("amount", amountInPaise);
        payload.put("currency", CURRENCY);
        payload.put("receipt", receipt);
        payload.put("payment_capture", 1);

        Map<String, String> notes = new LinkedHashMap<>();
        notes.put("support_type", supportType);
        payload.put("notes", notes);

        try {
            String requestBody = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/json")
                    .header("Authorization", basicAuthHeader())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException(extractRazorpayError(response.body()));
            }

            JsonNode json = objectMapper.readTree(response.body());
            RazorpayOrderResponse orderResponse = new RazorpayOrderResponse();
            orderResponse.setOrderId(json.path("id").asText());
            orderResponse.setAmount(normalizedAmount);
            orderResponse.setAmountInPaise(amountInPaise);
            orderResponse.setCurrency(json.path("currency").asText(CURRENCY));
            orderResponse.setReceipt(receipt);
            orderResponse.setKeyId(KEY_ID);
            return orderResponse;
        } catch (Exception e) {
            throw new RuntimeException("Unable to create Razorpay order", e);
        }
    }

    public boolean verifySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        ensureConfigured();
        try {
            String payload = razorpayOrderId + "|" + razorpayPaymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(KEY_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String computedSignature = toHex(digest);
            return computedSignature.equalsIgnoreCase(razorpaySignature);
        } catch (Exception e) {
            throw new RuntimeException("Unable to verify Razorpay signature", e);
        }
    }

    public String getKeyId() {
        return KEY_ID;
    }

    public boolean isConfigured() {
        return !"YOUR_RAZORPAY_KEY".equals(KEY_ID) && !"YOUR_RAZORPAY_SECRET".equals(KEY_SECRET);
    }

    private void ensureConfigured() {
        if (!isConfigured()) {
            throw new IllegalStateException(
                    "Razorpay keys are not configured. Replace YOUR_RAZORPAY_KEY and YOUR_RAZORPAY_SECRET.");
        }
    }

    private String basicAuthHeader() {
        String credentials = KEY_ID + ":" + KEY_SECRET;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    private String extractRazorpayError(String responseBody) {
        try {
            JsonNode json = objectMapper.readTree(responseBody);
            JsonNode errorNode = json.path("error");
            if (!errorNode.isMissingNode()) {
                String description = errorNode.path("description").asText(null);
                if (description != null && !description.isBlank()) {
                    return description;
                }
            }
        } catch (Exception ignored) {
            // Fall through to generic message.
        }
        return "Razorpay order creation failed";
    }

    private String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            builder.append(String.format("%02x", value));
        }
        return builder.toString();
    }

    public static class RazorpayOrderResponse {
        private String orderId;
        private BigDecimal amount;
        private long amountInPaise;
        private String currency;
        private String receipt;
        private String keyId;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public long getAmountInPaise() {
            return amountInPaise;
        }

        public void setAmountInPaise(long amountInPaise) {
            this.amountInPaise = amountInPaise;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getReceipt() {
            return receipt;
        }

        public void setReceipt(String receipt) {
            this.receipt = receipt;
        }

        public String getKeyId() {
            return keyId;
        }

        public void setKeyId(String keyId) {
            this.keyId = keyId;
        }
    }
}