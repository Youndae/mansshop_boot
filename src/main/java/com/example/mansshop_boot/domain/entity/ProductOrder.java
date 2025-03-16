package com.example.mansshop_boot.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "productOrder")
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Member member;

    @Column(length = 50,
            nullable = false
    )
    private String recipient;

    @Column(length = 100,
            nullable = false
    )
    private String orderPhone;

    @Column(length = 200,
            nullable = false
    )
    private String orderAddress;

    @Column(length = 200)
    private String orderMemo;

    @Column(nullable = false)
    private int orderTotalPrice;

    private int deliveryFee;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(length = 10,
            nullable = false
    )
    private String paymentType;

    @Column(length = 20,
            nullable = false
    )
    private String orderStat;

    @Column(nullable = false)
    private int productCount;

    @OneToMany(mappedBy = "productOrder", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private final List<ProductOrderDetail> productOrderDetailSet = new ArrayList<>();

    public void addDetail(ProductOrderDetail productOrderDetail) {
        productOrderDetailSet.add(productOrderDetail);
        productOrderDetail.setProductOrder(this);
    }

    public void setOrderStat(String orderStat) {
        this.orderStat = orderStat;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

}
