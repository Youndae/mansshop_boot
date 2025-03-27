package com.example.mansshop_boot.domain.enumuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisCaching {

    ADMIN_PRODUCT_QNA_COUNT("admin_product_QnA_count"),
    ADMIN_MEMBER_QNA_COUNT("admin_member_QnA_count"),
    ADMIN_ORDER_COUNT("admin_order_count");

    private final String key;
}
