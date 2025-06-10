package com.example.mansshop_boot.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "classification")
@ToString
public class Classification {

    @Id
    @Column(length = 100)
    private String id;

    @Column(nullable = false)
    private int classificationStep;
}
