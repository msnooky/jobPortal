package com.example.demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import jakarta.persistence.ManyToOne;

import java.sql.Timestamp;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    private Long freelancerId;

    private Long jobId;

    private Timestamp appliedAt = new Timestamp(System.currentTimeMillis());

}
