package com.IrvinCabello.user_api_test.controller;

import com.IrvinCabello.user_api_test.model.LoginRequest;
import com.IrvinCabello.user_api_test.model.User;
import com.IrvinCabello.user_api_test.repository.UserRepository;
import com.IrvinCabello.user_api_test.service.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EncryptionService encryptionService;  // ← nuevo

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        User user = userRepository.findByTaxId(loginRequest.getTax_id());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        }

        // ↓ Compara encriptando el password recibido y comparando con el almacenado
        if (!encryptionService.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        }

        return ResponseEntity.ok("Login successful");
    }
}