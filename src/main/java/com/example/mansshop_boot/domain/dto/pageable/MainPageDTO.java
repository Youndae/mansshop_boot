package com.example.mansshop_boot.domain.dto.pageable;

import com.example.mansshop_boot.domain.enumuration.PageAmount;
import lombok.Builder;

public record MainPageDTO(
        int pageNum
        , int mainProductAmount
        , String keyword
        , String classification
) {

    @Builder
    public MainPageDTO(int pageNum
                        , String keyword
                        , String classification) {
        this(
                pageNum
                , 12
                , keyword == null ? null : "%" + keyword + "%"
                , classification
        );
    }

    public MainPageDTO(String classification) {
        this(
                1,
                PageAmount.MAIN_AMOUNT.getAmount(),
                null,
                classification
        );
    }
}
