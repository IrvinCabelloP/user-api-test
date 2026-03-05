package com.IrvinCabello.user_api_test.service;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

@Service
public class ValidationService {

    private static final Pattern RFC_PATTERN =
        Pattern.compile("^[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}$");

    private static final Pattern ANDRES_FORMAT_PATTERN =
        Pattern.compile("^(\\+\\d{1,3}[\\s\\-]?)?(\\d[\\s\\-]?){9}\\d$");

    /**
     * Validación de formato RFC mexicano.
     */
    public boolean isValidRfc(String taxId) {
        if (taxId == null || taxId.isBlank()) return false;
        return RFC_PATTERN.matcher(taxId.trim().toUpperCase()).matches();
    }

    /**
     * AndresFormat: 10 dígitos locales, código de país opcional.
    */
    public boolean isValidPhone(String phone) {
        if (phone == null || phone.isBlank()) return false;

        String trimmed = phone.trim();

        if (!ANDRES_FORMAT_PATTERN.matcher(trimmed).matches()) return false;

        String digitsOnly = trimmed.replaceAll("[^\\d]", "");
        
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