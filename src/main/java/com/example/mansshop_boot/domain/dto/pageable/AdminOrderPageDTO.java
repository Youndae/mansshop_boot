package com.example.mansshop_boot.domain.dto.pageable;

public record AdminOrderPageDTO(
        String keyword
        , String searchType
        , int page
        , int amount
) {
    public AdminOrderPageDTO(String keyword, String searchType, int page) {
        this(
                keyword == null ? null : "%" + keyword + "%"
                , searchType
                , page
                , 20
        );

    }
}
