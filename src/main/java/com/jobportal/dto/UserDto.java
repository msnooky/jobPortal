package com.jobportal.dto;

import lombok.Getter;

import java.beans.ConstructorProperties;

@Getter
public class UserDto {
    private String name;
    private String email;
    private String password;
    private String role;

    @ConstructorProperties({"name", "email", "password", "role"})
    public UserDto(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
