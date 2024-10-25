package com.jobportal.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Entity
@Table(name = "user")
@Data
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return the user's authorities (roles)
        return null; // Implement based on your roles setup
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Implement based on your business logic
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Implement based on your business logic
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Implement based on your business logic
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    // Getters and setters
}
