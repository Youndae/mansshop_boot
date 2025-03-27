package com.example.mansshop_boot.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "productReview")
@ToString
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Lob
    @Column(columnDefinition = "TEXT",
            nullable = false
    )
    private String reviewContent;

    @ManyToOne
    @JoinColumn(name = "productOptionId", nullable = false)
    private ProductOption productOption;

    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3)")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3)")
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 0",
            nullable = false
    )
    private boolean status;

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
