package com.smartbilling.dao;

import com.smartbilling.model.InvoiceItem;
import com.smartbilling.util.DBConnection;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for InvoiceItem entity.
 * Handles invoice line item operations using JDBC.
 */
@Repository
public class InvoiceItemDAO {

    /**
     * Insert an invoice item using an existing connection (for transaction support).
     *
     * @return the generated item ID
     */
    public int insert(InvoiceItem item, Connection conn) throws SQLException {
        String sql = "INSERT INTO invoice_items (invoice_id, product_id, quantity, price, gst_percentage, total) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, item.getInvoiceId());
            ps.setInt(2, item.getProductId());
            ps.setInt(3, item.getQuantity());
            ps.setBigDecimal(4, item.getPrice());
            ps.setBigDecimal(5, item.getGstPercentage());
            ps.setBigDecimal(6, item.getTotal());
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
     * Find all items for a given invoice, including product names via JOIN.
     */
    public List<InvoiceItem> findByInvoiceId(int invoiceId) {
        List<InvoiceItem> items = new ArrayList<>();
        String sql = "SELECT ii.id, ii.invoice_id, ii.product_id, ii.quantity, ii.price, " +
                     "ii.gst_percentage, ii.total, p.name AS product_name " +
                     "FROM invoice_items ii " +
                     "JOIN products p ON ii.product_id = p.id " +
                     "WHERE ii.invoice_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToInvoiceItem(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching items for invoice: " + invoiceId, e);
        }
        return items;
    }

    /**
     * Map a ResultSet row to an InvoiceItem object.
     */
    private InvoiceItem mapResultSetToInvoiceItem(ResultSet rs) throws SQLException {
        InvoiceItem item = new InvoiceItem();
        item.setId(rs.getInt("id"));
        item.setInvoiceId(rs.getInt("invoice_id"));
        item.setProductId(rs.getInt("product_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setPrice(rs.getBigDecimal("price"));
        item.setGstPercentage(rs.getBigDecimal("gst_percentage"));
        item.setTotal(rs.getBigDecimal("total"));
        try {
            item.setProductName(rs.getString("product_name"));
        } catch (SQLException ignored) {
            // product_name column may not be present in all queries
        }
        return item;
    }
}
