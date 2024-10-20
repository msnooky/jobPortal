package com.example.demo.dto;

import lombok.Value;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Optional;

@Value
public class FreelancerDto {

    List<String> skills;
    Long salary;
    String location;

    @ConstructorProperties({"skills",
            "salaryRange",
            "location",
            "profileVisibility"})
    public FreelancerDto(List<String> skills,
                         Long salary,
                         String location) {
        this.skills = skills;
        this.salary = salary;
        this.location = location;
    }


}
