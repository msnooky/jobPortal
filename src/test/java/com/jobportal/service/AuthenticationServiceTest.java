package com.jobportal.service;

import com.jobportal.dto.UserDto;
import com.jobportal.models.User;
import com.jobportal.repository.UserRepository;
import com.jobportal.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDto = new UserDto("John Doe", "john.doe@example.com",
                "password", "Freelancer");

        user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password");
        user.setRole("ROLE_USER");
    }

    @Test
    void registerUser_ShouldRegisterNewUser() throws Exception {
        when(userService.checkUserPresence(userDto.getEmail())).thenReturn(Optional.empty());

        authenticationService.registerUser(userDto);

        verify(userService, times(1)).saveNewUser(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailAlreadyInUse() {
        when(userService.checkUserPresence(userDto.getEmail())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(Exception.class, () -> authenticationService.registerUser(userDto));

        assertEquals("Email is already in use", exception.getMessage());
    }

    @Test
    void authenticate_ShouldReturnToken_WhenCredentialsAreValid() {
        when(userService.findUserByEmail(userDto.getEmail())).thenReturn(user);
        when(jwtUtil.generateToken(user.getName())).thenReturn("valid.jwt.token");

        String token = authenticationService.authenticate(userDto.getEmail(), userDto.getPassword());

        assertEquals("valid.jwt.token", token);
    }

    @Test
    void authenticate_ShouldThrowException_WhenCredentialsAreInvalid() {
        when(userService.findUserByEmail(userDto.getEmail())).thenReturn(user);

        Exception exception = assertThrows(RuntimeException.class, () -> authenticationService.authenticate(userDto.getEmail(), "wrongPassword"));

        assertEquals("Invalid credentials", exception.getMessage());
    }
}
