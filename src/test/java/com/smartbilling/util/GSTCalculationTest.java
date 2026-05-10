package com.smartbilling.util;

import com.smartbilling.model.InvoiceItem;
import com.smartbilling.service.InvoiceService;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GST calculation logic.
 * Tests various GST rates and edge cases.
 */
class GSTCalculationTest {

    @Test
    @DisplayName("GST at 18% on ₹1000 should be ₹180")
    void testGST18Percent() {
        BigDecimal base = new BigDecimal("1000.00");
        BigDecimal gst = new BigDecimal("18.00");
        BigDecimal result = InvoiceService.calculateGST(base, gst);
        assertEquals(new BigDecimal("180.00"), result);
    }

    @Test
    @DisplayName("GST at 12% on ₹5000 should be ₹600")
    void testGST12Percent() {
        BigDecimal base = new BigDecimal("5000.00");
        BigDecimal gst = new BigDecimal("12.00");
        BigDecimal result = InvoiceService.calculateGST(base, gst);
        assertEquals(new BigDecimal("600.00"), result);
    }

    @Test
    @DisplayName("GST at 5% on ₹2500 should be ₹125")
    void testGST5Percent() {
        BigDecimal base = new BigDecimal("2500.00");
        BigDecimal gst = new BigDecimal("5.00");
        BigDecimal result = InvoiceService.calculateGST(base, gst);
        assertEquals(new BigDecimal("125.00"), result);
    }

    @Test
    @DisplayName("GST at 28% on ₹3000 should be ₹840")
    void testGST28Percent() {
        BigDecimal base = new BigDecimal("3000.00");
        BigDecimal gst = new BigDecimal("28.00");
        BigDecimal result = InvoiceService.calculateGST(base, gst);
        assertEquals(new BigDecimal("840.00"), result);
    }

    @Test
    @DisplayName("GST at 0% should be ₹0")
    void testGSTZeroPercent() {
        BigDecimal base = new BigDecimal("1000.00");
        BigDecimal gst = BigDecimal.ZERO;
        BigDecimal result = InvoiceService.calculateGST(base, gst);
        assertEquals(new BigDecimal("0.00"), result);
    }

    @Test
    @DisplayName("GST on zero amount should be ₹0")
    void testGSTZeroAmount() {
        BigDecimal result = InvoiceService.calculateGST(BigDecimal.ZERO, new BigDecimal("18.00"));
        assertEquals(new BigDecimal("0.00"), result);
    }

    @Test
    @DisplayName("GST should round correctly (half-up)")
    void testGSTRounding() {
        // 333.33 * 18% = 59.9994 -> should round to 60.00
        BigDecimal base = new BigDecimal("333.33");
        BigDecimal gst = new BigDecimal("18.00");
        BigDecimal result = InvoiceService.calculateGST(base, gst);
        assertEquals(new BigDecimal("60.00"), result);
    }

    @Test
    @DisplayName("InvoiceItem GST amount calculation should be correct")
    void testInvoiceItemGSTAmount() {
        InvoiceItem item = new InvoiceItem();
        item.setPrice(new BigDecimal("500.00"));
        item.setQuantity(3);
        item.setGstPercentage(new BigDecimal("18.00"));

        // Base = 500 * 3 = 1500, GST = 1500 * 18 / 100 = 270
        BigDecimal expectedGst = new BigDecimal("270.00");
        assertEquals(expectedGst, item.getGstAmount());
    }

    @Test
    @DisplayName("InvoiceItem base amount calculation should be correct")
    void testInvoiceItemBaseAmount() {
        InvoiceItem item = new InvoiceItem();
        item.setPrice(new BigDecimal("250.00"));
        item.setQuantity(4);

        BigDecimal expectedBase = new BigDecimal("1000.00");
        assertEquals(0, expectedBase.compareTo(item.getBaseAmount()));
    }

    @Test
    @DisplayName("Grand total should be subtotal + GST")
    void testGrandTotalCalculation() {
        BigDecimal subtotal = new BigDecimal("10000.00");
        BigDecimal gstRate = new BigDecimal("18.00");
        BigDecimal gst = InvoiceService.calculateGST(subtotal, gstRate);
        BigDecimal grandTotal = subtotal.add(gst);

        assertEquals(new BigDecimal("11800.00"), grandTotal);
    }
}
