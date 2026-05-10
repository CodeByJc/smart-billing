package com.smartbilling.dao;

import com.smartbilling.model.SupportPayment;
import com.smartbilling.util.DBConnection;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * JDBC DAO for support payment transactions.
 */
@Repository
public class SupportPaymentDAO {

    public int insert(SupportPayment payment) {
        String sql = "INSERT INTO support_payments " +
                "(support_type, amount, razorpay_order_id, razorpay_payment_id, payment_status) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, payment.getSupportType());
            ps.setBigDecimal(2, payment.getAmount());
            ps.setString(3, payment.getRazorpayOrderId());
            ps.setString(4, payment.getRazorpayPaymentId());
            ps.setString(5, payment.getPaymentStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    payment.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting support payment record", e);
        }
        return -1;
    }

    public void updateSuccess(String razorpayOrderId, String razorpayPaymentId) {
        updateStatus(razorpayOrderId, razorpayPaymentId, "SUCCESS");
    }

    public void updateFailed(String razorpayOrderId, String razorpayPaymentId) {
        updateStatus(razorpayOrderId, razorpayPaymentId, "FAILED");
    }

    public SupportPayment findByOrderId(String razorpayOrderId) {
        String sql = "SELECT id, support_type, amount, razorpay_order_id, razorpay_payment_id, payment_status, created_at "
                +
                "FROM support_payments WHERE razorpay_order_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, razorpayOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding support payment by order ID: " + razorpayOrderId, e);
        }
        return null;
    }

    public SupportPayment findByPaymentId(String razorpayPaymentId) {
        String sql = "SELECT id, support_type, amount, razorpay_order_id, razorpay_payment_id, payment_status, created_at "
                +
                "FROM support_payments WHERE razorpay_payment_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, razorpayPaymentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding support payment by payment ID: " + razorpayPaymentId, e);
        }
        return null;
    }

    private void updateStatus(String razorpayOrderId, String razorpayPaymentId, String status) {
        String sql = "UPDATE support_payments SET razorpay_payment_id = ?, payment_status = ? WHERE razorpay_order_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, razorpayPaymentId);
            ps.setString(2, status);
            ps.setString(3, razorpayOrderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating support payment status for order ID: " + razorpayOrderId, e);
        }
    }

    private SupportPayment mapRow(ResultSet rs) throws SQLException {
        SupportPayment payment = new SupportPayment();
        payment.setId(rs.getInt("id"));
        payment.setSupportType(rs.getString("support_type"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setRazorpayOrderId(rs.getString("razorpay_order_id"));
        payment.setRazorpayPaymentId(rs.getString("razorpay_payment_id"));
        payment.setPaymentStatus(rs.getString("payment_status"));
        payment.setCreatedAt(rs.getTimestamp("created_at"));
        return payment;
    }
}