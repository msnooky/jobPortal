package com.jobportal.controller;

import com.jobportal.dto.UserDto;
import com.jobportal.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() throws Exception {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", "test@example.com");
        loginData.put("password", "password123");

        String expectedToken = "mockToken";
        when(authenticationService.authenticate("test@example.com", "password123"))
                .thenReturn(expectedToken);

        ResponseEntity<Map<String, String>> response = authenticationController.login(loginData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedToken, response.getBody().get("token"));
    }

    @Test
    void testLoginFailure() throws Exception {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", "test@example.com");
        loginData.put("password", "wrongpassword");

        when(authenticationService.authenticate("test@example.com", "wrongpassword"))
                .thenThrow(new RuntimeException("Invalid credentials"));

        ResponseEntity<Map<String, String>> response = authenticationController.login(loginData);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody().get("error"));
    }

    @Test
    void testSignupSuccess() throws Exception {
        UserDto userDto = new UserDto("Test User", "test@example.com", "password123", "Freelancer");

        String expectedToken = "mockToken";
        doNothing().when(authenticationService).registerUser(userDto);
        when(authenticationService.authenticate("test@example.com", "password123")).thenReturn(expectedToken);

        ResponseEntity<Map<String, String>> response = authenticationController.signup(userDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully", response.getBody().get("message"));
        assertEquals(expectedToken, response.getBody().get("token"));
    }

    @Test
    void testSignupFailure() throws Exception {
        UserDto userDto = new UserDto(null, "test@example.com", "password123", null);

        doThrow(new Exception("Email already in use")).when(authenticationService).registerUser(userDto);

        ResponseEntity<Map<String, String>> response = authenticationController.signup(userDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email already in use", response.getBody().get("error"));
    }
}
