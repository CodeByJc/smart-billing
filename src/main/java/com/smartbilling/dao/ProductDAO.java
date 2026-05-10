package com.smartbilling.dao;

import com.smartbilling.model.Product;
import com.smartbilling.util.DBConnection;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Product entity.
 * Handles product CRUD, search, and stock operations using JDBC.
 */
@Repository
public class ProductDAO {

    /**
     * Retrieve all products ordered by creation date (newest first).
     */
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, name, description, price, gst_percentage, stock_quantity, created_at " +
                     "FROM products ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all products", e);
        }
        return products;
    }

    /**
     * Find a product by its ID.
     */
    public Product findById(int id) {
        String sql = "SELECT id, name, description, price, gst_percentage, stock_quantity, created_at " +
                     "FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding product by id: " + id, e);
        }
        return null;
    }

    /**
     * Find a product by its ID using an existing connection (for transactions).
     */
    public Product findById(int id, Connection conn) throws SQLException {
        String sql = "SELECT id, name, description, price, gst_percentage, stock_quantity, created_at " +
                     "FROM products WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        }
        return null;
    }

    /**
     * Search products by name (case-insensitive partial match).
     */
    public List<Product> search(String keyword) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, name, description, price, gst_percentage, stock_quantity, created_at " +
                     "FROM products WHERE name LIKE ? ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching products with keyword: " + keyword, e);
        }
        return products;
    }

    /**
     * Insert a new product into the database.
     *
     * @return the generated product ID
     */
    public int insert(Product product) {
        String sql = "INSERT INTO products (name, description, price, gst_percentage, stock_quantity) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setBigDecimal(4, product.getGstPercentage());
            ps.setInt(5, product.getStockQuantity());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting product: " + product.getName(), e);
        }
        return -1;
    }

    /**
     * Update an existing product.
     */
    public void update(Product product) {
        String sql = "UPDATE products SET name = ?, description = ?, price = ?, " +
                     "gst_percentage = ?, stock_quantity = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setBigDecimal(4, product.getGstPercentage());
            ps.setInt(5, product.getStockQuantity());
            ps.setInt(6, product.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating product: " + product.getId(), e);
        }
    }

    /**
     * Delete a product by its ID.
     */
    public void delete(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting product: " + id, e);
        }
    }

    /**
     * Deduct stock quantity for a product (used during invoice creation).
     * Uses an existing connection for transaction support.
     */
    public void deductStock(int productId, int quantity, Connection conn) throws SQLException {
        String sql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ? AND stock_quantity >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Insufficient stock for product ID: " + productId);
            }
        }
    }

    /**
     * Get the total count of products.
     */
    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM products";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting product count", e);
        }
        return 0;
    }

    /**
     * Get the count of products with low stock (fewer than 10 units).
     */
    public int getLowStockCount() {
        String sql = "SELECT COUNT(*) FROM products WHERE stock_quantity < 10";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting low stock count", e);
        }
        return 0;
    }

    /**
     * Get all products with low stock (fewer than 10 units).
     */
    public List<Product> findLowStockProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, name, description, price, gst_percentage, stock_quantity, created_at " +
                     "FROM products WHERE stock_quantity < 10 ORDER BY stock_quantity ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching low stock products", e);
        }
        return products;
    }

    /**
     * Map a ResultSet row to a Product object.
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setGstPercentage(rs.getBigDecimal("gst_percentage"));
        product.setStockQuantity(rs.getInt("stock_quantity"));
        product.setCreatedAt(rs.getTimestamp("created_at"));
        return product;
    }
}
