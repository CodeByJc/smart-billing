package com.smartbilling.controller;

import com.smartbilling.model.Invoice;
import com.smartbilling.service.InvoiceService;
import com.smartbilling.util.PDFGenerator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.OutputStream;
import java.util.List;

/**
 * Controller for invoice viewing, history, and PDF generation.
 */
@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    /**
     * View a specific invoice with its line items.
     */
    @GetMapping("/view/{id}")
    public String viewInvoice(@PathVariable int id, Model model) {
        try {
            Invoice invoice = invoiceService.getInvoiceWithItems(id);
            model.addAttribute("invoice", invoice);
            model.addAttribute("activePage", "invoices");
            return "invoice";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Invoice not found");
            return "redirect:/invoices/history";
        }
    }

    /**
     * Display invoice history (all invoices).
     */
    @GetMapping("/history")
    public String showInvoiceHistory(Model model) {
        List<Invoice> invoices = invoiceService.getAllInvoices();
        model.addAttribute("invoices", invoices);
        model.addAttribute("activePage", "invoices");
        return "invoice-history";
    }

    /**
     * Download invoice as PDF.
     */
    @GetMapping("/pdf/{id}")
    public void downloadPDF(@PathVariable int id, HttpServletResponse response) {
        try {
            Invoice invoice = invoiceService.getInvoiceWithItems(id);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition",
                    "attachment; filename=Invoice_" + invoice.getInvoiceNumber() + ".pdf");

            OutputStream out = response.getOutputStream();
            PDFGenerator.generateInvoice(invoice, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }
    }
}
