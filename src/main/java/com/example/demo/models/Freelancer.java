package com.example.demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import jakarta.persistence.*;;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Freelancer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long freelancerId;
    private Long userId;
    private Long salary;
    private String location;

}
