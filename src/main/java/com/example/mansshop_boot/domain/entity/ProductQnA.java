package com.example.mansshop_boot.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "productQnA")
public class ProductQnA {

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
    @Column(columnDefinition = "TEXT")
    private String qnaContent;

    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3)")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3)")
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 0",
            nullable = false
    )
    private boolean productQnAStat;

    public void setProductQnAStat(boolean productQnAStat) {
        this.productQnAStat = productQnAStat;
    }

    @Override
    public String toString() {
        return "ProductQnA{" +
                "id=" + id +
                ", member=" + member +
                ", qnaContent='" + qnaContent + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", productQnAStat=" + productQnAStat +
                ", productName=" + product.getProductName() +
                '}';
    }
}
