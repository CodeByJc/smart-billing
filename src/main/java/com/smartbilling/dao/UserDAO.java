package com.smartbilling.dao;

import com.smartbilling.model.User;
import com.smartbilling.util.DBConnection;
import org.springframework.stereotype.Repository;

import java.sql.*;

/**
 * Data Access Object for User entity.
 * Handles user CRUD operations using JDBC with PreparedStatement.
 */
@Repository
public class UserDAO {

    /**
     * Find a user by username.
     *
     * @param username the username to search for
     * @return the User if found, null otherwise
     */
    public User findByUsername(String username) {
        String sql = "SELECT id, username, password, role, created_at FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by username: " + username, e);
        }
        return null;
    }

    /**
     * Find a user by ID.
     *
     * @param id the user ID
     * @return the User if found, null otherwise
     */
    public User findById(int id) {
        String sql = "SELECT id, username, password, role, created_at FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by id: " + id, e);
        }
        return null;
    }

    /**
     * Insert a new user into the database.
     *
     * @param user the user to insert (password should already be hashed)
     * @return the generated user ID
     */
    public int insert(User user) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole() != null ? user.getRole() : "ADMIN");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting user: " + user.getUsername(), e);
        }
        return -1;
    }

    /**
     * Map a ResultSet row to a User object.
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}
