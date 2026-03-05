package com.IrvinCabello.user_api_test.controller;

import com.IrvinCabello.user_api_test.model.User;
import com.IrvinCabello.user_api_test.repository.UserRepository;
import com.IrvinCabello.user_api_test.service.EncryptionService;
import com.IrvinCabello.user_api_test.service.ValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private EncryptionService encryptionService;

    @MockBean
    private ValidationService validationService;

    // ─────────────────────────────────────────────
    // GET /users
    // ─────────────────────────────────────────────

    @Test
    void getUsers_returnsOk() throws Exception {
        when(userRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // ─────────────────────────────────────────────
    // POST /users
    // ─────────────────────────────────────────────

    @Test
    void createUser_validData_returns201() throws Exception {
        when(validationService.isValidRfc(anyString())).thenReturn(true);
        when(validationService.isValidPhone(anyString())).thenReturn(true);
        when(validationService.getMadagascarTimestamp()).thenReturn("05-03-2026 14:00");
        when(userRepository.findByTaxId(anyString())).thenReturn(null);
        when(encryptionService.encrypt(anyString())).thenReturn("encrypted_password");

        String body = """
                {
                  "name": "Luis",
                  "email": "luis@mail.com",
                  "tax_id": "LUIS010101ABC",
                  "password": "secret123",
                  "phone": "+52 555 123 4567"
                }
                """;

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void createUser_missingFields_returns400() throws Exception {
        String body = """
                {
                  "name": "Luis"
                }
                """;

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_invalidRfc_returns400() throws Exception {
        when(validationService.isValidRfc(anyString())).thenReturn(false);

        String body = """
                {
                  "name": "Luis",
                  "email": "luis@mail.com",
                  "tax_id": "INVALIDO",
                  "password": "secret123",
                  "phone": "+52 555 123 4567"
                }
                """;

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_invalidPhone_returns400() throws Exception {
        when(validationService.isValidRfc(anyString())).thenReturn(true);
        when(validationService.isValidPhone(anyString())).thenReturn(false);

        String body = """
                {
                  "name": "Luis",
                  "email": "luis@mail.com",
                  "tax_id": "LUIS010101ABC",
                  "password": "secret123",
                  "phone": "123"
                }
                """;

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_duplicateTaxId_returns409() throws Exception {
        when(validationService.isValidRfc(anyString())).thenReturn(true);
        when(validationService.isValidPhone(anyString())).thenReturn(true);

        User existing = new User();
        existing.setTax_id("LUIS010101ABC");
        when(userRepository.findByTaxId("LUIS010101ABC")).thenReturn(existing);

        String body = """
                {
                  "name": "Luis",
                  "email": "luis@mail.com",
                  "tax_id": "LUIS010101ABC",
                  "password": "secret123",
                  "phone": "+52 555 123 4567"
                }
                """;

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isConflict());
    }

    // ─────────────────────────────────────────────
    // PATCH /users/{id}
    // ─────────────────────────────────────────────

    @Test
    void updateUser_notFound_returns404() throws Exception {
        when(userRepository.findById(any(UUID.class))).thenReturn(null);

        String body = """
                { "email": "nuevo@mail.com" }
                """;

        mockMvc.perform(patch("/users/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────────────────────────
    // DELETE /users/{id}
    // ─────────────────────────────────────────────

    @Test
    void deleteUser_notFound_returns404() throws Exception {
        when(userRepository.deleteById(any(UUID.class))).thenReturn(false);

        mockMvc.perform(delete("/users/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_valid_returns200() throws Exception {
        when(userRepository.deleteById(any(UUID.class))).thenReturn(true);

        mockMvc.perform(delete("/users/" + UUID.randomUUID()))
                .andExpect(status().isOk());
    }
}