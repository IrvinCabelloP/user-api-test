package com.IrvinCabello.user_api_test.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceTest {

    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new ValidationService();
    }

    // ─────────────────────────────────────────────
    // RFC Tests
    // ─────────────────────────────────────────────

    @Test
    void testValidRfc_4letras() {
        assertTrue(validationService.isValidRfc("AARR990101XXX"));
    }

    @Test
    void testValidRfc_3letras() {
        assertTrue(validationService.isValidRfc("AAR990101XXX"));
    }

    @Test
    void testValidRfc_lowercase_shouldPass() {
        assertTrue(validationService.isValidRfc("aarr990101xxx"));
    }

    @Test
    void testInvalidRfc_tooShort() {
        assertFalse(validationService.isValidRfc("AA990101XXX"));
    }

    @Test
    void testInvalidRfc_wrongFormat() {
        assertFalse(validationService.isValidRfc("INVALIDO"));
    }

    @Test
    void testInvalidRfc_null() {
        assertFalse(validationService.isValidRfc(null));
    }

    @Test
    void testInvalidRfc_blank() {
        assertFalse(validationService.isValidRfc("   "));
    }

    // ─────────────────────────────────────────────
    // AndresFormat Phone Tests
    // ─────────────────────────────────────────────

    @Test
    void testValidPhone_10digits() {
        assertTrue(validationService.isValidPhone("5551234567"));
    }

    @Test
    void testValidPhone_withCountryCodeMX() {
        assertTrue(validationService.isValidPhone("+52 555 123 4567"));
    }

    @Test
    void testValidPhone_withCountryCodeUS() {
        assertTrue(validationService.isValidPhone("+1 555 123 4567"));
    }

    @Test
    void testValidPhone_withDashes() {
        assertTrue(validationService.isValidPhone("555-123-4567"));
    }

    @Test
    void testInvalidPhone_tooShort() {
        assertFalse(validationService.isValidPhone("12345"));
    }

    @Test
    void testInvalidPhone_null() {
        assertFalse(validationService.isValidPhone(null));
    }

    @Test
    void testInvalidPhone_blank() {
        assertFalse(validationService.isValidPhone(""));
    }

    @Test
    void testInvalidPhone_letters() {
        assertFalse(validationService.isValidPhone("abcdefghij"));
    }

    // ─────────────────────────────────────────────
    // Madagascar Timestamp Tests
    // ─────────────────────────────────────────────

    @Test
    void testMadagascarTimestamp_notNull() {
        assertNotNull(validationService.getMadagascarTimestamp());
    }

    @Test
    void testMadagascarTimestamp_format() {
        String ts = validationService.getMadagascarTimestamp();
        // Formato esperado: dd-MM-yyyy HH:mm  (ej: 05-03-2026 14:30)
        assertTrue(ts.matches("\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}"),
                "Timestamp format should be dd-MM-yyyy HH:mm but was: " + ts);
    }
}