package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Setter
@Getter
@Table(name = "employer")
@AllArgsConstructor
@NoArgsConstructor
public class Employer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employerId;
    private Long userId;
    private String companyName;

    public Object getJobs() {
        return new Object();
    }

}
