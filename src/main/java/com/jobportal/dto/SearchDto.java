package com.jobportal.dto;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Optional;

public class SearchDto {
    List<String> skills;
    String location;
    Long minSalary;
    Long maxSalary;

    @ConstructorProperties({"skills", "location", "minSalary", "maxSalary"})
    public SearchDto(List<String> skills, String location, Long minSalary, Long maxSalary) {
        this.skills = skills;
        this.location = location;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
    }

    public List<String> getSkills() {
        return skills;
    }

    public Optional<String> getLocation() {
        return Optional.ofNullable(location);
    }

    public Optional<Long> getMinSalary() {
        return Optional.ofNullable(minSalary);
    }

    public Optional<Long> getMaxSalary() {
        return Optional.ofNullable(maxSalary);
    }
}
