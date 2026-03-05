package com.IrvinCabello.user_api_test.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionServiceTest {

    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        encryptionService = new EncryptionService();
    }

    @Test
    void testEncrypt_notNull() {
        assertNotNull(encryptionService.encrypt("secret123"));
    }

    @Test
    void testEncrypt_notEqualToPlainText() {
        String plain = "secret123";
        String encrypted = encryptionService.encrypt(plain);
        assertNotEquals(plain, encrypted);
    }

    @Test
    void testEncrypt_sameInputSameOutput() {
        // AES-ECB es determinista: misma entrada = mismo resultado
        String a = encryptionService.encrypt("secret123");
        String b = encryptionService.encrypt("secret123");
        assertEquals(a, b);
    }

    @Test
    void testEncrypt_differentInputsDifferentOutputs() {
        String a = encryptionService.encrypt("password1");
        String b = encryptionService.encrypt("password2");
        assertNotEquals(a, b);
    }

    @Test
    void testMatches_correctPassword() {
        String encrypted = encryptionService.encrypt("mypassword");
        assertTrue(encryptionService.matches("mypassword", encrypted));
    }

    @Test
    void testMatches_wrongPassword() {
        String encrypted = encryptionService.encrypt("mypassword");
        assertFalse(encryptionService.matches("wrongpassword", encrypted));
    }

    @Test
    void testEncrypt_isBase64() {
        String encrypted = encryptionService.encrypt("test");
        // Base64 solo contiene A-Z, a-z, 0-9, +, /, =
        assertTrue(encrypted.matches("^[A-Za-z0-9+/=]+$"));
    }
}