package com.example.demo.dto;

import lombok.Value;

import java.beans.ConstructorProperties;

@Value
public class EmployerDto {

    String companyName;

    @ConstructorProperties({"companyName"})
    public EmployerDto(String companyName) {
        this.companyName = companyName;
    }
}
