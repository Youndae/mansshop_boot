package com.example.mansshop_boot.domain.dto.main.out;

import com.example.mansshop_boot.domain.dto.main.business.MainListDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메인 상품 목록 응답 데이터")
public record MainListResponseDTO(
        @Schema(description = "상품 아이디")
        String productId,
        @Schema(description = "상품명")
        String productName,
        @Schema(description = "상품 대표 썸네일")
        String thumbnail,
        @Schema(description = "상품 원가")
        int originPrice,
        @Schema(description = "상품 할인율(%)")
        int discount,
        @Schema(description = "상품 할인 적용가")
        int discountPrice,
        @Schema(description = "상품 품절 여부")
        boolean isSoldOut
) {
    public MainListResponseDTO(MainListDTO dto) {
        this(
                dto.productId()
                , dto.productName()
                , dto.thumbnail()
                , dto.price()
                , dto.discount()
                , (int) (dto.price() * (1 - ((double) dto.discount() / 100)))
                , dto.stock() == 0
        );
    }
}
