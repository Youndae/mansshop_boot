package com.example.mansshop_boot.domain.dto.cart.in;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "장바구니 담기 요청 데이터", type = "array")
public record AddCartDTO(
        @Schema(name = "optionId", description = "상품 옵션 아이디")
        Long optionId,
        @Schema(name = "count", description = "상품 개수")
        int count
        /*, int price
        , List<AddCartDTO> addList*/
) {
}
