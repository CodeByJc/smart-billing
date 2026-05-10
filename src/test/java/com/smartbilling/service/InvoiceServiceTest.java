package com.smartbilling.service;

import com.smartbilling.dao.InvoiceDAO;
import com.smartbilling.dao.InvoiceItemDAO;
import com.smartbilling.dao.ProductDAO;
import com.smartbilling.model.Product;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InvoiceService.
 * Tests validation, GST calculation, and error handling.
 */
@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceDAO invoiceDAO;

    @Mock
    private InvoiceItemDAO invoiceItemDAO;

    @Mock
    private ProductDAO productDAO;

    @InjectMocks
    private InvoiceService invoiceService;

    @Test
    @DisplayName("Should throw exception for empty customer name")
    void testEmptyCustomerName() {
        assertThrows(IllegalArgumentException.class, () -> {
            invoiceService.createInvoice("", "Cash", new int[]{1}, new int[]{1});
        });
    }

    @Test
    @DisplayName("Should throw exception for null customer name")
    void testNullCustomerName() {
        assertThrows(IllegalArgumentException.class, () -> {
            invoiceService.createInvoice(null, "Cash", new int[]{1}, new int[]{1});
        });
    }

    @Test
    @DisplayName("Should throw exception for invalid payment type")
    void testInvalidPaymentType() {
        assertThrows(IllegalArgumentException.class, () -> {
            invoiceService.createInvoice("Test", "Credit", new int[]{1}, new int[]{1});
        });
    }

    @Test
    @DisplayName("Should throw exception for empty product list")
    void testEmptyProductList() {
        assertThrows(IllegalArgumentException.class, () -> {
            invoiceService.createInvoice("Test", "Cash", new int[]{}, new int[]{});
        });
    }

    @Test
    @DisplayName("Should throw exception for mismatched arrays")
    void testMismatchedArrays() {
        assertThrows(IllegalArgumentException.class, () -> {
            invoiceService.createInvoice("Test", "Cash", new int[]{1, 2}, new int[]{1});
        });
    }

    @Test
    @DisplayName("Static GST calculation should be correct")
    void testGSTCalculation() {
        BigDecimal base = new BigDecimal("1000.00");
        BigDecimal gstRate = new BigDecimal("18.00");
        BigDecimal expected = new BigDecimal("180.00");

        BigDecimal result = InvoiceService.calculateGST(base, gstRate);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("GST calculation with zero base should return zero")
    void testGSTCalculationZeroBase() {
        BigDecimal result = InvoiceService.calculateGST(BigDecimal.ZERO, new BigDecimal("18.00"));
        assertEquals(new BigDecimal("0.00"), result);
    }

    @Test
    @DisplayName("GST calculation with null should return zero")
    void testGSTCalculationNull() {
        assertEquals(BigDecimal.ZERO, InvoiceService.calculateGST(null, null));
        assertEquals(BigDecimal.ZERO, InvoiceService.calculateGST(new BigDecimal("100"), null));
        assertEquals(BigDecimal.ZERO, InvoiceService.calculateGST(null, new BigDecimal("18")));
    }
}
