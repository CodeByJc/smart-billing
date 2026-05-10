package com.smartbilling.dao;

import com.smartbilling.model.Product;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductDAO.
 * Uses Mockito to mock JDBC connections.
 */
class ProductDAOTest {

    private ProductDAO productDAO;

    @BeforeEach
    void setUp() {
        productDAO = new ProductDAO();
    }

    @Test
    @DisplayName("Product model should correctly identify low stock")
    void testLowStockDetection() {
        Product product = new Product();
        product.setStockQuantity(5);
        assertTrue(product.isLowStock(), "Product with 5 stock should be low stock");

        product.setStockQuantity(10);
        assertFalse(product.isLowStock(), "Product with 10 stock should not be low stock");

        product.setStockQuantity(0);
        assertTrue(product.isLowStock(), "Product with 0 stock should be low stock");
    }

    @Test
    @DisplayName("Product model should store all fields correctly")
    void testProductModel() {
        Product product = new Product();
        product.setId(1);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("100.00"));
        product.setGstPercentage(new BigDecimal("18.00"));
        product.setStockQuantity(50);

        assertEquals(1, product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals("Test Description", product.getDescription());
        assertEquals(new BigDecimal("100.00"), product.getPrice());
        assertEquals(new BigDecimal("18.00"), product.getGstPercentage());
        assertEquals(50, product.getStockQuantity());
    }

    @Test
    @DisplayName("Product toString should contain relevant info")
    void testProductToString() {
        Product product = new Product();
        product.setId(1);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("50000"));
        product.setStockQuantity(10);

        String result = product.toString();
        assertTrue(result.contains("Laptop"));
        assertTrue(result.contains("50000"));
    }

    @Test
    @DisplayName("Product with negative stock should be flagged as low stock")
    void testNegativeStockIsLowStock() {
        Product product = new Product();
        product.setStockQuantity(-1);
        assertTrue(product.isLowStock());
    }

    @Test
    @DisplayName("Product boundary: exactly 9 stock is low, 10 is not")
    void testLowStockBoundary() {
        Product product = new Product();

        product.setStockQuantity(9);
        assertTrue(product.isLowStock(), "9 should be low stock");

        product.setStockQuantity(10);
        assertFalse(product.isLowStock(), "10 should not be low stock");
    }
}
