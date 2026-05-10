package com.smartbilling.dao;

import com.smartbilling.model.Invoice;
import com.smartbilling.util.DBConnection;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Invoice entity.
 * Handles invoice CRUD and aggregate queries using JDBC.
 */
@Repository
public class InvoiceDAO {

    /**
     * Insert a new invoice using an existing connection (for transaction support).
     *
     * @return the generated invoice ID
     */
    public int insert(Invoice invoice, Connection conn) throws SQLException {
        String sql = "INSERT INTO invoices (invoice_number, customer_name, subtotal, gst_amount, " +
                     "total_amount, payment_type) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, invoice.getInvoiceNumber());
            ps.setString(2, invoice.getCustomerName());
            ps.setBigDecimal(3, invoice.getSubtotal());
            ps.setBigDecimal(4, invoice.getGstAmount());
            ps.setBigDecimal(5, invoice.getTotalAmount());
            ps.setString(6, invoice.getPaymentType());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    /**
     * Find an invoice by its ID, including basic fields.
     */
    public Invoice findById(int id) {
        String sql = "SELECT id, invoice_number, customer_name, subtotal, gst_amount, " +
                     "total_amount, payment_type, created_at FROM invoices WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInvoice(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding invoice by id: " + id, e);
        }
        return null;
    }

    /**
     * Find an invoice by its invoice number.
     */
    public Invoice findByInvoiceNumber(String invoiceNumber) {
        String sql = "SELECT id, invoice_number, customer_name, subtotal, gst_amount, " +
                     "total_amount, payment_type, created_at FROM invoices WHERE invoice_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, invoiceNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInvoice(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding invoice: " + invoiceNumber, e);
        }
        return null;
    }

    /**
     * Retrieve all invoices ordered by creation date (newest first).
     */
    public List<Invoice> findAll() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT id, invoice_number, customer_name, subtotal, gst_amount, " +
                     "total_amount, payment_type, created_at FROM invoices ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all invoices", e);
        }
        return invoices;
    }

    /**
     * Retrieve the most recent invoices (limited count).
     */
    public List<Invoice> findRecent(int limit) {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT id, invoice_number, customer_name, subtotal, gst_amount, " +
                     "total_amount, payment_type, created_at FROM invoices ORDER BY created_at DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching recent invoices", e);
        }
        return invoices;
    }

    /**
     * Get the total number of invoices.
     */
    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM invoices";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting invoice count", e);
        }
        return 0;
    }

    /**
     * Get the total sales amount across all invoices.
     */
    public BigDecimal getTotalSales() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM invoices";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting total sales", e);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Get the next invoice number in the sequence.
     * Format: INV-YYYYMMDD-XXXX
     */
    public String getNextInvoiceNumber(Connection conn) throws SQLException {
        String sql = "SELECT MAX(invoice_number) FROM invoices WHERE invoice_number LIKE ?";
        java.time.LocalDate today = java.time.LocalDate.now();
        String datePrefix = "INV-" + today.toString().replace("-", "") + "-";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, datePrefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getString(1) != null) {
                    String lastNumber = rs.getString(1);
                    int seq = Integer.parseInt(lastNumber.substring(lastNumber.lastIndexOf('-') + 1));
                    return datePrefix + String.format("%04d", seq + 1);
                }
            }
        }
        return datePrefix + "0001";
    }

    /**
     * Map a ResultSet row to an Invoice object.
     */
    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setId(rs.getInt("id"));
        invoice.setInvoiceNumber(rs.getString("invoice_number"));
        invoice.setCustomerName(rs.getString("customer_name"));
        invoice.setSubtotal(rs.getBigDecimal("subtotal"));
        invoice.setGstAmount(rs.getBigDecimal("gst_amount"));
        invoice.setTotalAmount(rs.getBigDecimal("total_amount"));
        invoice.setPaymentType(rs.getString("payment_type"));
        invoice.setCreatedAt(rs.getTimestamp("created_at"));
        return invoice;
    }
}
