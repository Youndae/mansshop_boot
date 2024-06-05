package com.example.mansshop_boot.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long salesRate;

    private long sales;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String productName;

    private long optionId;

    public void setSales(long sales) {
        this.sales = this.sales + sales;
    }

    public void setSalesRate(long salesRate) {
        this.salesRate = this.salesRate + salesRate;
    }
}
