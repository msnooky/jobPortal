package com.jobportal.service;

import com.jobportal.dto.UserDto;
import com.jobportal.models.User;
import com.jobportal.repository.UserRepository;
import com.jobportal.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;
import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Registers a new user.
     *
     * @param userDto The user DTO containing the user's information.
     * @throws Exception If the email is already in use.
     */
    public void registerUser(UserDto userDto) throws Exception {
        Optional<User> user = userService.checkUserPresence(userDto.getEmail());
        if (user.isPresent()) {
            throw new Exception("Email is already in use");
        }

        User newUser = new User();
        newUser.setName(userDto.getName());
        newUser.setEmail(userDto.getEmail());
        newUser.setPassword(userDto.getPassword());
        newUser.setRole(userDto.getRole());
        userService.saveNewUser(newUser);
    }

    /**
     * Authenticates a user.
     *
     * @param email    The user's email.
     * @param password The user's password.
     * @return The JWT token if the credentials are valid.
     * @throws RuntimeException If the credentials are invalid.
     */
    public String authenticate(String email, String password) {
        User user = userService.findUserByEmail(email);
        if (user.getPassword().equals(password)) {
            return jwtUtil.generateToken(user.getName());
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}
