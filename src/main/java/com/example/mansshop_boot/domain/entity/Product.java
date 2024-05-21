package com.example.mansshop_boot.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "classificationId")
    private Classification classification;

    private String productName;

    private long productPrice;

    private String thumbnail;

    private int closed;

    private Long productSales;

    private int productDiscount;

    private Date createdAt;

    private Date updatedAt;
}
