package com.IrvinCabello.user_api_test.controller;

import com.IrvinCabello.user_api_test.model.User;
import com.IrvinCabello.user_api_test.repository.UserRepository;
import com.IrvinCabello.user_api_test.service.EncryptionService;
import com.IrvinCabello.user_api_test.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private ValidationService validationService;

    // ─────────────────────────────────────────────
    // GET /users?sortedBy=&filter=
    // ─────────────────────────────────────────────
    @GetMapping
    public List<User> getAllUsers(
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String filter) {

        List<User> userList = new ArrayList<>(userRepository.findAll());

        if (sortedBy != null && !sortedBy.isEmpty()) {
            switch (sortedBy.toLowerCase()) {
                case "id":         userList.sort(Comparator.comparing(User::getId));                                        break;
                case "name":       userList.sort(Comparator.comparing(User::getName,       String.CASE_INSENSITIVE_ORDER)); break;
                case "email":      userList.sort(Comparator.comparing(User::getEmail,      String.CASE_INSENSITIVE_ORDER)); break;
                case "tax_id":     userList.sort(Comparator.comparing(User::getTax_id,     String.CASE_INSENSITIVE_ORDER)); break;
                case "phone":      userList.sort(Comparator.comparing(User::getPhone));                                     break;
                case "created_at": userList.sort(Comparator.comparing(User::getCreated_at));                                break;
            }
        }

        if (filter != null && !filter.isEmpty()) {
            String[] parts  = filter.split(" ", 3);
            String field    = parts[0];
            String operator = parts[1];
            String value    = parts[2];

            userList = userList.stream()
                .filter(u -> {
                    String fieldValue;
                    switch (field.toLowerCase()) {
                        case "name":       fieldValue = u.getName();          break;
                        case "email":      fieldValue = u.getEmail();         break;
                        case "phone":      fieldValue = u.getPhone();         break;
                        case "tax_id":     fieldValue = u.getTax_id();        break;
                        case "created_at": fieldValue = u.getCreated_at();    break;
                        case "id":         fieldValue = u.getId().toString(); break;
                        default: return false;
                    }
                    switch (operator.toLowerCase()) {
                        case "co": return fieldValue.toLowerCase().contains(value.toLowerCase());
                        case "eq": return fieldValue.equalsIgnoreCase(value);
                        case "sw": return fieldValue.toLowerCase().startsWith(value.toLowerCase());
                        case "ew": return fieldValue.toLowerCase().endsWith(value.toLowerCase());
                        default: return false;
                    }
                })
                .collect(Collectors.toList());
        }

        return userList;
    }

    // ─────────────────────────────────────────────
    // POST /users — Agregar nuevo usuario
    // ─────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User newUser) {

        // Campos obligatorios
        if (newUser.getName()     == null || newUser.getName().isBlank() ||
            newUser.getEmail()    == null || newUser.getEmail().isBlank() ||
            newUser.getTax_id()   == null || newUser.getTax_id().isBlank() ||
            newUser.getPassword() == null || newUser.getPassword().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Fields name, email, tax_id and password are required.");
        }

        // Validar formato RFC
        if (!validationService.isValidRfc(newUser.getTax_id())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid tax_id: must follow RFC format (e.g. AARR990101XXX).");
        }

        // Validar phone — AndresFormat (solo si viene en el request)
        if (newUser.getPhone() != null && !newUser.getPhone().isBlank()) {
            if (!validationService.isValidPhone(newUser.getPhone())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid phone (AndresFormat): 10 local digits required, country code optional (e.g. +52 555 123 4567).");
            }
        }

        // tax_id único
        if (userRepository.findByTaxId(newUser.getTax_id()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A user with that tax_id already exists.");
        }

        // Asignar UUID automático
        newUser.setId(UUID.randomUUID());

        // Encriptar password con AES-256
        newUser.setPassword(encryptionService.encrypt(newUser.getPassword()));

        // Timestamp en zona horaria de Madagascar (UTC+3), formato dd-MM-yyyy HH:mm
        newUser.setCreated_at(validationService.getMadagascarTimestamp());

        userRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    // ─────────────────────────────────────────────
    // PATCH /users/{id} — Actualizar atributos por ID
    // ─────────────────────────────────────────────
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> fields) {

        User user = userRepository.findById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with id: " + id);
        }

        // Validar tax_id si viene en el body
        if (fields.containsKey("tax_id")) {
            String newTaxId = (String) fields.get("tax_id");
            if (!validationService.isValidRfc(newTaxId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid tax_id: must follow RFC format.");
            }
            if (!newTaxId.equalsIgnoreCase(user.getTax_id()) &&
                userRepository.findByTaxId(newTaxId) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("A user with that tax_id already exists.");
            }
        }

        // Validar phone si viene en el body
        if (fields.containsKey("phone")) {
            String newPhone = (String) fields.get("phone");
            if (!validationService.isValidPhone(newPhone)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid phone (AndresFormat): 10 local digits required, country code optional.");
            }
        }

        fields.forEach((key, value) -> {
            switch (key.toLowerCase()) {
                case "name":       user.setName((String) value);    break;
                case "email":      user.setEmail((String) value);   break;
                case "phone":      user.setPhone((String) value);   break;
                case "tax_id":     user.setTax_id((String) value);  break;
                case "password":   user.setPassword(encryptionService.encrypt((String) value)); break;
                case "created_at": user.setCreated_at((String) value); break;
            }
        });

        return ResponseEntity.ok(user);
    }

    // ─────────────────────────────────────────────
    // DELETE /users/{id} — Eliminar usuario por ID
    // ─────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        boolean removed = userRepository.deleteById(id);
        if (!removed) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with id: " + id);
        }
        return ResponseEntity.ok("User deleted successfully.");
    }
}