package com.smartbilling.service;

import com.smartbilling.dao.UserDAO;
import com.smartbilling.model.User;
import com.smartbilling.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service layer for authentication operations.
 * Handles login validation with plain password comparison.
 */
@Service
public class AuthService {

    @Autowired
    private UserDAO userDAO;

    /**
     * Authenticate a user by username and password.
     *
     * @param username the username
     * @param password the plain-text password
     * @return the authenticated User, or null if authentication fails
     */
    public User authenticate(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return null;
        }

        User user = userDAO.findByUsername(username.trim());
        if (user == null) {
            return null;
        }

        // Support both BCrypt-hashed passwords and legacy plain-text records.
        if (PasswordUtil.verifyPassword(password, user.getPassword()) ||
                password.equals(user.getPassword())) {
            return user;
        }

        return null;
    }

    /**
     * Register a new admin user.
     *
     * @param username the username
     * @param password the plain-text password
     * @return the created User ID
     */
    public int registerUser(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Username and password are required");
        }

        // Check if username already exists
        User existing = userDAO.findByUsername(username.trim());
        if (existing != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(PasswordUtil.hashPassword(password));
        user.setRole("ADMIN");

        return userDAO.insert(user);
    }
}
