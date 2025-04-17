package com.example.mansshop_boot.domain.entity;

import com.example.mansshop_boot.domain.dto.order.in.OrderProductDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "productSalesSummary")
@ToString
public class ProductSalesSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate periodMonth;

    @ManyToOne
    @JoinColumn(name = "classificationId", nullable = false)
    private Classification classification;

    @ManyToOne
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "productOptionId", nullable = false)
    private ProductOption productOption;

    @Column(nullable = false)
    private long sales;

    @Column(nullable = false)
    private long salesQuantity;

    @Column(nullable = false)
    private long orderQuantity;


    public void setPatchSalesData(OrderProductDTO orderProductDTO) {
        this.sales += orderProductDTO.getDetailPrice();
        this.salesQuantity += orderProductDTO.getDetailCount();
        this.orderQuantity += 1;
    }
}
