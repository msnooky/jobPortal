package com.example.demo.service;

import com.example.demo.models.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public String authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getPassword().equals(password)) {
            return jwtUtil.generateToken(user.getName());
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}
