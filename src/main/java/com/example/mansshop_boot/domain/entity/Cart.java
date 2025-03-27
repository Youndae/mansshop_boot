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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart")
@ToString
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Member member;

    @Column(length = 255)
    private String cookieId;

    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3)")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3)")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "cart", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private final List<CartDetail> cartDetailList = new ArrayList<>();

    public void addCartDetail(CartDetail detail) {
        cartDetailList.add(detail);
        detail.setCart(this);
    }
}