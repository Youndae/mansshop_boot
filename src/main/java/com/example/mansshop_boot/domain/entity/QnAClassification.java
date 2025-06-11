package com.example.mansshop_boot.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "qnaClassification")
@ToString
public class QnAClassification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100,
            nullable = false
    )
    private String qnaClassificationName;
}
