package com.example.mansshop_boot.domain.dto.order.business;

public record ProductSalesQuantityPatchDTO(
        String productId,
        int salesQuantity
) {
}
