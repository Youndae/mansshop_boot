package com.example.mansshop_boot.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "productThumbnail")
@ToString
public class ProductThumbnail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Column(length = 255,
            nullable = false
    )
    private String imageName;

    public void setProduct(Product product) {
        this.product = product;
    }
}
