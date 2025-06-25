package com.example.mansshop_boot.controller.fixture.domain;

import com.example.mansshop_boot.domain.dto.order.in.OrderProductDTO;
import com.example.mansshop_boot.domain.entity.Product;
import com.example.mansshop_boot.domain.entity.ProductOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record MemberPaymentMapDTO(
        int totalPrice,
        int totalCount,
        List<OrderProductDTO> orderProductFixtureList,
        Map<String, Product> paymentProductMap,
        Map<String, Long> paymentProductSalesQuantityMap,
        Map<Long, ProductOption> paymentProductOptionMap,
        Map<Long, Long> paymentProductOptionStockMap
) {
}
