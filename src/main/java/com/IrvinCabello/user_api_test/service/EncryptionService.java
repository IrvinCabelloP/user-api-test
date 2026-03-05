package com.IrvinCabello.user_api_test.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class EncryptionService {

    // Clave de 32 bytes = 256 bits (AES-256)
    private static final String SECRET_KEY = "MySecretKey12345MySecretKey12345";

    /**
     * Encripta un texto plano con AES-256/ECB/PKCS5Padding
     * y devuelve el resultado en Base64.
     */
    public String encrypt(String plainText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting password", e);
        }
    }

    /**
     * Encripta el texto candidato y lo compara con el hash almacenado.
     * Se usa en el login para verificar la contraseña.
     */
    public boolean matches(String plainText, String encryptedStored) {
        return encrypt(plainText).equals(encryptedStored);
    }
}