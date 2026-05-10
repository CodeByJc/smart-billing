package com.smartbilling.service;

import com.smartbilling.dao.ProductDAO;
import com.smartbilling.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service layer for product management operations.
 * Provides business logic validation on top of DAO operations.
 */
@Service
public class ProductService {

    @Autowired
    private ProductDAO productDAO;

    /**
     * Get all products.
     */
    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    /**
     * Get a product by ID.
     *
     * @throws IllegalArgumentException if the product is not found
     */
    public Product getProductById(int id) {
        Product product = productDAO.findById(id);
        if (product == null) {
            throw new IllegalArgumentException("Product not found with ID: " + id);
        }
        return product;
    }

    /**
     * Search products by name keyword.
     */
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productDAO.findAll();
        }
        return productDAO.search(keyword.trim());
    }

    /**
     * Add a new product with validation.
     *
     * @return the generated product ID
     */
    public int addProduct(Product product) {
        validateProduct(product);
        return productDAO.insert(product);
    }

    /**
     * Update an existing product with validation.
     */
    public void updateProduct(Product product) {
        validateProduct(product);
        // Verify product exists
        Product existing = productDAO.findById(product.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Product not found with ID: " + product.getId());
        }
        productDAO.update(product);
    }

    /**
     * Delete a product by ID.
     */
    public void deleteProduct(int id) {
        Product existing = productDAO.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Product not found with ID: " + id);
        }
        productDAO.delete(id);
    }

    /**
     * Get total product count.
     */
    public int getTotalCount() {
        return productDAO.getTotalCount();
    }

    /**
     * Get count of low-stock products.
     */
    public int getLowStockCount() {
        return productDAO.getLowStockCount();
    }

    /**
     * Get all products with low stock.
     */
    public List<Product> getLowStockProducts() {
        return productDAO.findLowStockProducts();
    }

    /**
     * Validate product fields.
     */
    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        if (product.getGstPercentage() == null || product.getGstPercentage().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("GST percentage cannot be negative");
        }
        if (product.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
    }
}
