package com.smartbilling.controller;

import com.smartbilling.model.Invoice;
import com.smartbilling.model.Product;
import com.smartbilling.service.InvoiceService;
import com.smartbilling.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for billing operations.
 * Handles billing page display and invoice creation.
 */
@Controller
@RequestMapping("/billing")
public class BillingController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private ProductService productService;

    /**
     * Display the billing page with product list for selection.
     */
    @GetMapping
    public String showBillingPage(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        model.addAttribute("activePage", "billing");
        return "billing";
    }

    /**
     * Process invoice creation from the billing form.
     * Receives parallel arrays of productId and quantity.
     */
    @PostMapping("/create")
    public String createInvoice(@RequestParam String customerName,
                                 @RequestParam String paymentType,
                                 @RequestParam("productId") int[] productIds,
                                 @RequestParam("quantity") int[] quantities,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        try {
            Invoice invoice = invoiceService.createInvoice(
                    customerName, paymentType, productIds, quantities);

            redirectAttributes.addFlashAttribute("success",
                    "Invoice " + invoice.getInvoiceNumber() + " created successfully!");
            return "redirect:/invoices/view/" + invoice.getId();

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/billing";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error creating invoice: " + e.getMessage());
            return "redirect:/billing";
        }
    }
}
