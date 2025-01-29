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
@Table(name = "productReviewReply")
public class ProductReviewReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "reviewId", nullable = false)
    private ProductReview productReview;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String replyContent;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDate createdAt;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDate updatedAt;
}
