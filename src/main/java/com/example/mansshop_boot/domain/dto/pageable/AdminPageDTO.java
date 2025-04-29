package com.example.mansshop_boot.domain.dto.pageable;

import com.example.mansshop_boot.domain.enumeration.PageAmount;

public record AdminPageDTO(
        String keyword
        , int page
        , int amount
        , long offset
) {

    public AdminPageDTO(String keyword, int page) {
        this(
                keyword == null ? null : "%" + keyword + "%"
                , page
                , PageAmount.DEFAULT_AMOUNT.getAmount()
                , (long) (page - 1) * PageAmount.DEFAULT_AMOUNT.getAmount()
        );
    }
}
