package com.example.mansshop_boot.domain.dto.order.in;

import io.swagger.v3.oas.annotations.media.Schema;

public record OrderProductRequestDTO(
        @Schema(name = "optionId", description = "상품 옵션 아이디")
        long optionId,
        @Schema(name = "count", description = "상품 수량")
        int count
) {
}
