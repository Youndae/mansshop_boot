package com.example.mansshop_boot.domain.dto.pageable;

public record ProductDetailPageDTO(
        int pageNum
        , int reviewAmount
        , int qnaAmount
) {
    public ProductDetailPageDTO() {
        this(1, 10, 10);
    }
}
