package com.example.mansshop_boot.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

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
    private Date createdAt;

    private String paymentType;

    private int orderStat;

}
