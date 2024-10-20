package com.example.demo.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

import java.beans.ConstructorProperties;

@Builder
@Getter
public class JobDto {

    private Long id;
    private String title;
    private String description;
    private String location;
    private Long salary;
    private String tags;

    @ConstructorProperties({"id",
            "title",
            "description",
            "location",
            "salary",
            "tags"})
    public JobDto(Long id,String title, String description, String location, Long salary, String tags) {
        this.id=id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.salary = salary;
        this.tags = tags;
    }
}