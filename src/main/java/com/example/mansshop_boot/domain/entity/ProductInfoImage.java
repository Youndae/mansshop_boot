package com.example.mansshop_boot.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfoImage {

    @Id
    private String infoImageId;

    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;

    private String imageName;
}
