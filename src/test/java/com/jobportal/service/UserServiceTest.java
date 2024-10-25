package com.jobportal.service;

import com.jobportal.models.User;
import com.jobportal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("testUser");
        user.setEmail("test@example.com");
        user.setRole("Employer");
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        when(userRepository.findByName("testUser")).thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername("testUser");

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByName("unknownUser")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.loadUserByUsername("unknownUser"));
    }

    @Test
    void testFindUserByUsername_UserFound() {
        when(userRepository.findByName("testUser")).thenReturn(Optional.of(user));

        User result = userService.findUserByUsername("testUser");

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void testFindUserByUsername_UserNotFound() {
        when(userRepository.findByName("unknownUser")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.findUserByUsername("unknownUser"));
    }

    @Test
    void testDeleteEmployer() {
        userService.deleteEmployer(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetNameByUserId_UserFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getNameByUserId(1L);

        assertTrue(result.isPresent());
        assertEquals("testUser", result.get().getUsername());
    }

    @Test
    void testGetNameByUserId_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getNameByUserId(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindUserByEmail_UserFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = userService.findUserByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testFindUserByEmail_UserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.findUserByEmail("unknown@example.com"));
    }

    @Test
    void testCheckUserPresence_UserFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.checkUserPresence("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void testCheckUserPresence_UserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.checkUserPresence("unknown@example.com");

        assertFalse(result.isPresent());
    }

    @Test
    void testSaveNewUser() {
        userService.saveNewUser(user);

        verify(userRepository, times(1)).save(user);
    }
}
