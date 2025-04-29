package com.example.mansshop_boot.domain.dto.pageable;

import com.example.mansshop_boot.domain.enumeration.PageAmount;

public record AdminOrderPageDTO(
        String keyword
        , String searchType
        , int page
        , int amount
        , long offset
) {
    public AdminOrderPageDTO(String keyword, String searchType, int page) {
        this(
                keyword
                , searchType
                , page
                , PageAmount.DEFAULT_AMOUNT.getAmount()
                , (long) (page - 1) * PageAmount.DEFAULT_AMOUNT.getAmount()
        );

    }
}
