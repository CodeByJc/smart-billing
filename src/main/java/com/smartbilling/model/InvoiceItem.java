package com.smartbilling.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * InvoiceItem entity representing the 'invoice_items' table.
 * Each item links a product to an invoice with quantity and calculated total.
 */
public class InvoiceItem {

    private int id;
    private int invoiceId;
    private int productId;
    private int quantity;
    private BigDecimal price;
    private BigDecimal gstPercentage;
    private BigDecimal total;

    /** Transient field: product name for display purposes */
    private String productName;

    public InvoiceItem() {
    }

    public InvoiceItem(int id, int invoiceId, int productId, int quantity,
                       BigDecimal price, BigDecimal gstPercentage, BigDecimal total) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.gstPercentage = gstPercentage;
        this.total = total;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getGstPercentage() {
        return gstPercentage;
    }

    public void setGstPercentage(BigDecimal gstPercentage) {
        this.gstPercentage = gstPercentage;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Calculate the GST amount for this line item.
     */
    public BigDecimal getGstAmount() {
        if (price == null || gstPercentage == null) return BigDecimal.ZERO;
        BigDecimal baseAmount = price.multiply(BigDecimal.valueOf(quantity));
        return baseAmount.multiply(gstPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate the base amount (price × quantity) without GST.
     */
    public BigDecimal getBaseAmount() {
        if (price == null) return BigDecimal.ZERO;
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return "InvoiceItem{id=" + id + ", productId=" + productId +
               ", qty=" + quantity + ", total=" + total + "}";
    }
}
