package com.smartbilling.service;

import com.smartbilling.dao.UserDAO;
import com.smartbilling.model.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService.
 * Tests login authentication logic.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Should authenticate valid credentials")
    void testValidAuthentication() {
        User user = new User();
        user.setId(1);
        user.setUsername("admin");
        user.setPassword("admin123");
        user.setRole("ADMIN");

        when(userDAO.findByUsername("admin")).thenReturn(user);

        User result = authService.authenticate("admin", "admin123");

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        verify(userDAO, times(1)).findByUsername("admin");
    }

    @Test
    @DisplayName("Should reject wrong password")
    void testWrongPassword() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("admin123");

        when(userDAO.findByUsername("admin")).thenReturn(user);

        User result = authService.authenticate("admin", "wrongpassword");
        assertNull(result);
    }

    @Test
    @DisplayName("Should reject non-existent user")
    void testNonExistentUser() {
        when(userDAO.findByUsername("unknown")).thenReturn(null);

        User result = authService.authenticate("unknown", "password");
        assertNull(result);
    }

    @Test
    @DisplayName("Should reject null username")
    void testNullUsername() {
        User result = authService.authenticate(null, "password");
        assertNull(result);
    }

    @Test
    @DisplayName("Should reject empty password")
    void testEmptyPassword() {
        User result = authService.authenticate("admin", "");
        assertNull(result);
    }

    @Test
    @DisplayName("Should reject blank username")
    void testBlankUsername() {
        User result = authService.authenticate("   ", "password");
        assertNull(result);
    }
}
