package com.smartbilling.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Invoice entity representing the 'invoices' table.
 * Contains customer info, totals, and associated line items.
 */
public class Invoice {

    private int id;
    private String invoiceNumber;
    private String customerName;
    private BigDecimal subtotal;
    private BigDecimal gstAmount;
    private BigDecimal totalAmount;
    private String paymentType;
    private Timestamp createdAt;

    /** Transient list of invoice items (not stored directly in invoices table) */
    private List<InvoiceItem> items = new ArrayList<>();

    public Invoice() {
    }

    public Invoice(int id, String invoiceNumber, String customerName,
                   BigDecimal subtotal, BigDecimal gstAmount, BigDecimal totalAmount,
                   String paymentType, Timestamp createdAt) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.customerName = customerName;
        this.subtotal = subtotal;
        this.gstAmount = gstAmount;
        this.totalAmount = totalAmount;
        this.paymentType = paymentType;
        this.createdAt = createdAt;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(BigDecimal gstAmount) {
        this.gstAmount = gstAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Invoice{id=" + id + ", invoiceNumber='" + invoiceNumber +
               "', customer='" + customerName + "', total=" + totalAmount + "}";
    }
}
