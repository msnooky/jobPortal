package com.jobportal.service;

import com.jobportal.models.User;
import com.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User findUserByUsername(String username) {
        return userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteEmployer(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> getNameByUserId(Long id) {
        return userRepository.findById(id);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Optional<User> checkUserPresence(String email) {
        return userRepository.findByEmail(email);
    }

    public void saveNewUser(User user) {
        userRepository.save(user);
    }
}
