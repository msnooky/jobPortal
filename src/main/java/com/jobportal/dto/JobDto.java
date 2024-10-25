package com.jobportal.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.beans.ConstructorProperties;
import java.util.List;

@Builder
@Data
public class JobDto {

    private Long id;
    private String title;
    private String description;
    private String location;
    private Long salary;
    private String tags;
    private List<String> skills;

    @ConstructorProperties({"id", "title", "description", "location", "salary", "tags", "skills"})
    public JobDto(Long id, String title, String description, String location, Long salary, String tags, List<String> skills) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.salary = salary;
        this.tags = tags;
        this.skills = skills;
    }
}