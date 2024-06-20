package com.example.mansshop_boot.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Member member;

    private String recipient;

    private String orderPhone;

    private String orderAddress;

    private String orderMemo;

    private int orderTotalPrice;

    private int deliveryFee;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private String paymentType;

    private int orderStat;

    @OneToMany(mappedBy = "productOrder", cascade = CascadeType.ALL)
    private final Set<ProductOrderDetail> productOrderDetailSet = new HashSet<>();

    public void addDetail(ProductOrderDetail productOrderDetail) {
        productOrderDetailSet.add(productOrderDetail);
        productOrderDetail.setProductOrder(this);
    }


}
