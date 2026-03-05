package com.IrvinCabello.user_api_test.service;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

@Service
public class ValidationService {

    // ─────────────────────────────────────────────────────────────
    // RFC México: 3-4 letras + 6 dígitos (AAMMDD) + 3 alfanuméricos
    // Ejemplos válidos: AARR990101XXX  ANDR850101ABC  ZULE700101XYZ
    // ─────────────────────────────────────────────────────────────
    private static final Pattern RFC_PATTERN =
        Pattern.compile("^[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}$");

    // ─────────────────────────────────────────────────────────────
    // AndresFormat:
    //   Basado en el enunciado: "10 digits, could include country code"
    //   - Código de país opcional: + seguido de 1-3 dígitos
    //   - Separadores opcionales: espacios o guiones
    //   - Exactamente 10 dígitos locales
    //
    // Ejemplos válidos:
    //   5551234567          → 10 dígitos sin código
    //   555-123-4567        → con separadores
    //   +52 555 123 4567    → con código de país MX
    //   +1 555 123 4567     → con código de país US
    //   +44-555-123-4567    → con código de país UK
    // ─────────────────────────────────────────────────────────────
    private static final Pattern ANDRES_FORMAT_PATTERN =
        Pattern.compile("^(\\+\\d{1,3}[\\s\\-]?)?(\\d[\\s\\-]?){9}\\d$");

    /**
     * Valida formato RFC mexicano.
     */
    public boolean isValidRfc(String taxId) {
        if (taxId == null || taxId.isBlank()) return false;
        return RFC_PATTERN.matcher(taxId.trim().toUpperCase()).matches();
    }

    /**
     * AndresFormat: 10 dígitos locales, código de país opcional.
     * Extrae solo los dígitos: si tiene código de país (1-3 dígitos extra),
     * los dígitos locales deben ser exactamente 10.
     */
    public boolean isValidPhone(String phone) {
        if (phone == null || phone.isBlank()) return false;

        String trimmed = phone.trim();

        // Verificar estructura general
        if (!ANDRES_FORMAT_PATTERN.matcher(trimmed).matches()) return false;

        // Extraer solo dígitos
        String digitsOnly = trimmed.replaceAll("[^\\d]", "");

        // Sin código de país: exactamente 10 dígitos
        // Con código de país: 11 (1 dígito CC) o 12 (2 dígitos CC) o 13 (3 dígitos CC)
        return digitsOnly.length() >= 10 && digitsOnly.length() <= 13;
    }

    /**
     * Timestamp actual en zona horaria de Madagascar (EAT = UTC+3).
     * Formato: dd-MM-yyyy HH:mm
     */
    public String getMadagascarTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("Indian/Antananarivo")); // UTC+3 Madagascar
        return sdf.format(new Date());
    }
}