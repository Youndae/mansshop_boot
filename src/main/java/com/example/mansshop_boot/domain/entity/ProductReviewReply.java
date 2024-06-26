package com.example.mansshop_boot.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductReviewReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "reviewId")
    private ProductReview productReview;

    private String replyContent;

    @CreationTimestamp
    private LocalDate createdAt;

    @CreationTimestamp
    private LocalDate updatedAt;
}
