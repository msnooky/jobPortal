package com.jobportal.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.beans.ConstructorProperties;
import java.util.List;

@Value
@Data
public class FreelancerDto {

    String name;
    List<String> skills;
    Long salary;
    String location;

    @ConstructorProperties({"name", "skills", "salaryRange", "location"})
    public FreelancerDto(String name, List<String> skills, Long salary, String location) {
        this.name = name;
        this.skills = skills;
        this.salary = salary;
        this.location = location;
    }

}
