package com.example.mansshop_boot.service.unit.fixture;

import com.example.mansshop_boot.domain.entity.Cart;
import com.example.mansshop_boot.domain.entity.CartDetail;

public class CartUnitFixture {

    public static Cart createCartFixture() {
        return Cart.builder()
                .id(1L)
                .member(MemberUnitFixture.createMemberFixture())
                .build();
    }

    public static CartDetail createCartDetailFixture() {
        return CartDetail.builder()
                .id(1L)
                .cart(createCartFixture())
                .cartCount(1)
                .build();
    }
}
