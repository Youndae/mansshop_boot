package com.example.mansshop_boot.domain.enumuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RabbitMQPrefix {

    EXCHANGE_ORDER("order"),
    QUEUE_ORDER_PRODUCT("orderProduct"),
    QUEUE_ORDER_PRODUCT_OPTION("orderProductOption"),
    QUEUE_PERIOD_SUMMARY("periodSalesSummary"),
    QUEUE_PRODUCT_SUMMARY("productSalesSummary"),
    QUEUE_ORDER_CART("orderCart");

    private final String key;
}
