package com.jobportal.controller;

import com.jobportal.dto.UserDto;
import com.jobportal.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000/")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Handles the login request.
     * 
     * @param loginData A map containing the user's email and password.
     * @return A ResponseEntity containing a token if authentication is successful, 
     *         or an error message if authentication fails.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginData) {
        try {
            String email = loginData.get("email");
            String password = loginData.get("password");
            String token = authenticationService.authenticate(email, password);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Handles the signup request.
     * 
     * @param userDto A UserDto object containing the user's registration information.
     * @return A ResponseEntity containing a success message and a token if registration 
     *         is successful, or an error message if registration fails.
     */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody UserDto userDto) {
        try {
            authenticationService.registerUser(userDto);
            String token = authenticationService.authenticate(userDto.getEmail(), userDto.getPassword());
            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}