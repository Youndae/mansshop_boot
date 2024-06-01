package com.example.mansshop_boot.domain.dto.pageable;

public record ProductDetailPageDTO(
        int pageNum
        , int reviewAmount
        , int qnaAmount
) {
    public ProductDetailPageDTO() {
        this(1, 10, 10);
    }

    public ProductDetailPageDTO(int page) {
        this(page, 10, 10);
    }
}
