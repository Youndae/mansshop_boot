package com.example.mansshop_boot.domain.dto.pageable;

import lombok.Builder;


public record OrderPageDTO(
        int pageNum
        , int orderAmount
        , String term
) {

    @Builder
    public OrderPageDTO(int pageNum, String term) {

        this(pageNum, 20, term);
    }
}
