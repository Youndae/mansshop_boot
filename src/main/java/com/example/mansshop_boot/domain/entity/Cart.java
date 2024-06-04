package com.example.mansshop_boot.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Member member;

    private String cookieId;

    private Date createdAt;

    private Date updatedAt;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private final Set<CartDetail> cartDetailSet = new HashSet<>();

    public void addCartDetail(CartDetail cartDetail) {
        cartDetailSet.add(cartDetail);
        cartDetail.setCart(this);
    }



}
