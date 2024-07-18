package com.ecommerce.ecomm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class LogoutController {

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Aqui você pode adicionar qualquer lógica adicional necessária para o logout
        return ResponseEntity.ok("Logged out successfully.");
    }

    @PostMapping("/logout-success")
    public ResponseEntity<?> logoutSuccess() {
        return ResponseEntity.ok("You have been logged out.");
    }
}
