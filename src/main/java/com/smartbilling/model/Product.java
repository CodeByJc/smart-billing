package com.smartbilling.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Product entity representing the 'products' table.
 * Contains pricing, GST, and stock information.
 */
public class Product {

    private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal gstPercentage;
    private int stockQuantity;
    private Timestamp createdAt;

    public Product() {
    }

    public Product(int id, String name, String description, BigDecimal price,
                   BigDecimal gstPercentage, int stockQuantity, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.gstPercentage = gstPercentage;
        this.stockQuantity = stockQuantity;
        this.createdAt = createdAt;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Check if the product has low stock (fewer than 10 units).
     */
    public boolean isLowStock() {
        return stockQuantity < 10;
    }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', price=" + price +
               ", stock=" + stockQuantity + "}";
    }
}
