package com.example.mansshop_boot.domain.dto.admin.out;

public record AdminProductSalesListDTO(
        String classification,
        String productId,
        String productName,
        long sales,
        long salesQuantity
) {
}
