package com.smartbilling.service;

import com.smartbilling.dao.InvoiceDAO;
import com.smartbilling.dao.InvoiceItemDAO;
import com.smartbilling.dao.ProductDAO;
import com.smartbilling.model.Invoice;
import com.smartbilling.model.InvoiceItem;
import com.smartbilling.model.Product;
import com.smartbilling.util.DBConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Service layer for invoice/billing operations.
 * Handles invoice creation with full transaction management,
 * including stock validation, deduction, and GST calculation.
 */
@Service
public class InvoiceService {

    @Autowired
    private InvoiceDAO invoiceDAO;

    @Autowired
    private InvoiceItemDAO invoiceItemDAO;

    @Autowired
    private ProductDAO productDAO;

    /**
     * Create a new invoice with transaction management.
     * 
     * Steps:
     * 1. Validate all items have sufficient stock
     * 2. Calculate line item totals with GST
     * 3. Generate invoice number
     * 4. Insert invoice record
     * 5. Insert all invoice items
     * 6. Deduct stock for each product
     * 7. Commit transaction (or rollback on failure)
     *
     * @param customerName the customer name
     * @param paymentType  the payment type (Cash/Online)
     * @param productIds   array of product IDs
     * @param quantities   array of quantities (parallel to productIds)
     * @return the created Invoice with items populated
     * @throws Exception if validation fails or a database error occurs
     */
    public Invoice createInvoice(String customerName, String paymentType,
                                  int[] productIds, int[] quantities) throws Exception {
        // Validate inputs
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        if (paymentType == null || (!paymentType.equals("Cash") && !paymentType.equals("Online"))) {
            throw new IllegalArgumentException("Payment type must be Cash or Online");
        }
        if (productIds == null || productIds.length == 0) {
            throw new IllegalArgumentException("At least one product is required");
        }
        if (quantities == null || quantities.length != productIds.length) {
            throw new IllegalArgumentException("Quantities must match product count");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            BigDecimal subtotal = BigDecimal.ZERO;
            BigDecimal totalGst = BigDecimal.ZERO;

            // Step 1: Validate stock and calculate totals
            InvoiceItem[] items = new InvoiceItem[productIds.length];
            for (int i = 0; i < productIds.length; i++) {
                if (quantities[i] <= 0) {
                    throw new IllegalArgumentException("Quantity must be greater than zero");
                }

                Product product = productDAO.findById(productIds[i], conn);
                if (product == null) {
                    throw new IllegalArgumentException("Product not found: ID " + productIds[i]);
                }
                if (product.getStockQuantity() < quantities[i]) {
                    throw new IllegalArgumentException(
                            "Insufficient stock for '" + product.getName() +
                            "'. Available: " + product.getStockQuantity() +
                            ", Requested: " + quantities[i]);
                }

                // Calculate line item amounts
                BigDecimal baseAmount = product.getPrice().multiply(BigDecimal.valueOf(quantities[i]));
                BigDecimal gstAmount = baseAmount.multiply(product.getGstPercentage())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal lineTotal = baseAmount.add(gstAmount);

                InvoiceItem item = new InvoiceItem();
                item.setProductId(productIds[i]);
                item.setQuantity(quantities[i]);
                item.setPrice(product.getPrice());
                item.setGstPercentage(product.getGstPercentage());
                item.setTotal(lineTotal);
                item.setProductName(product.getName());

                items[i] = item;
                subtotal = subtotal.add(baseAmount);
                totalGst = totalGst.add(gstAmount);
            }

            BigDecimal grandTotal = subtotal.add(totalGst);

            // Step 2: Generate invoice number
            String invoiceNumber = invoiceDAO.getNextInvoiceNumber(conn);

            // Step 3: Create invoice record
            Invoice invoice = new Invoice();
            invoice.setInvoiceNumber(invoiceNumber);
            invoice.setCustomerName(customerName.trim());
            invoice.setSubtotal(subtotal);
            invoice.setGstAmount(totalGst);
            invoice.setTotalAmount(grandTotal);
            invoice.setPaymentType(paymentType);

            int invoiceId = invoiceDAO.insert(invoice, conn);
            invoice.setId(invoiceId);

            // Step 4: Insert invoice items and deduct stock
            for (InvoiceItem item : items) {
                item.setInvoiceId(invoiceId);
                invoiceItemDAO.insert(item, conn);
                productDAO.deductStock(item.getProductId(), item.getQuantity(), conn);
            }

            // Step 5: Commit transaction
            conn.commit();

            invoice.setItems(List.of(items));
            return invoice;

        } catch (Exception e) {
            DBConnection.rollback(conn);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
                DBConnection.closeConnection(conn);
            }
        }
    }

    /**
     * Get an invoice with its items populated.
     */
    public Invoice getInvoiceWithItems(int invoiceId) {
        Invoice invoice = invoiceDAO.findById(invoiceId);
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice not found with ID: " + invoiceId);
        }
        invoice.setItems(invoiceItemDAO.findByInvoiceId(invoiceId));
        return invoice;
    }

    /**
     * Get all invoices (without items).
     */
    public List<Invoice> getAllInvoices() {
        return invoiceDAO.findAll();
    }

    /**
     * Get recent invoices (limited count, without items).
     */
    public List<Invoice> getRecentInvoices(int limit) {
        return invoiceDAO.findRecent(limit);
    }

    /**
     * Get total invoice count.
     */
    public int getTotalCount() {
        return invoiceDAO.getTotalCount();
    }

    /**
     * Get total sales amount.
     */
    public BigDecimal getTotalSales() {
        return invoiceDAO.getTotalSales();
    }

    /**
     * Calculate GST amount for a given base amount and GST percentage.
     * Utility method that can be used for testing and validation.
     *
     * @param baseAmount    the pre-tax amount
     * @param gstPercentage the GST rate (e.g., 18.00 for 18%)
     * @return the GST amount
     */
    public static BigDecimal calculateGST(BigDecimal baseAmount, BigDecimal gstPercentage) {
        if (baseAmount == null || gstPercentage == null) {
            return BigDecimal.ZERO;
        }
        return baseAmount.multiply(gstPercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
