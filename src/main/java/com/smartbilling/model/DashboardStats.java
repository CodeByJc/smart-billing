package com.smartbilling.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Dashboard statistics model used to aggregate data for the dashboard view.
 * This is a transient model — not mapped to any database table.
 */
public class DashboardStats {

    private int totalProducts;
    private BigDecimal totalSales;
    private int totalInvoices;
    private int lowStockCount;
    private List<Invoice> recentInvoices;
    private List<Product> lowStockProducts;

    public DashboardStats() {
    }

    // Getters and Setters

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public int getTotalInvoices() {
        return totalInvoices;
    }

    public void setTotalInvoices(int totalInvoices) {
        this.totalInvoices = totalInvoices;
    }

    public int getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(int lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public List<Invoice> getRecentInvoices() {
        return recentInvoices;
    }

    public void setRecentInvoices(List<Invoice> recentInvoices) {
        this.recentInvoices = recentInvoices;
    }

    public List<Product> getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(List<Product> lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }
}
