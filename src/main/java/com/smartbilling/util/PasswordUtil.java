package com.smartbilling.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Password hashing utility using BCrypt.
 * Provides methods to hash and verify passwords securely.
 */
public class PasswordUtil {

    private static final int BCRYPT_ROUNDS = 10;

    /**
     * Hash a plain-text password using BCrypt.
     *
     * @param plainPassword the password to hash
     * @return the BCrypt hashed password
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * Verify a plain-text password against a BCrypt hash.
     *
     * @param plainPassword  the password to verify
     * @param hashedPassword the BCrypt hash to check against
     * @return true if the password matches the hash
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}
