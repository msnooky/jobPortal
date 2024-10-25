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
    /**
     * Loads the user details by username.
     * @param username the username to search for
     * @return the user details if found
     * @throws UsernameNotFoundException if the user is not found
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Finds a user by their username.
     * @param username the username to search for
     * @return the user if found
     * @throws RuntimeException if the user is not found
     */
    public User findUserByUsername(String username) {
        return userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Deletes an employer by their ID.
     * @param id the
 ID of the employer to delete
     */
    public void deleteEmployer(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Gets the name of a user by their ID.
     * @param id the ID of the user to search for
     * @return an Optional containing the user's name if found
     */
    public Optional<User> getNameByUserId(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Finds a user by their email address.
     * @param email the email address to search for
     * @return the user if found
     * @throws RuntimeException if the user is not found
     */
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Checks if a user with the given email address exists.
     * @param email the email address to check
     * @return an Optional containing the user if found
     */
    public Optional<User> checkUserPresence(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Saves a new user to the database.
     * @param user the user to save
     */
    public void saveNewUser(User user) {
        userRepository.save(user);
    }
}
