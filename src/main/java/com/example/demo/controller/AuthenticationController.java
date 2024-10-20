package com.example.demo.controller;

import com.example.demo.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> loginData) {
        try {
            String email = loginData.get("email");
            String password = loginData.get("password");
            String token = authenticationService.authenticate(email, password);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }
}

