package com.example.mansshop_boot.domain.dto.pageable;

public record AdminPageDTO(
        String keyword
        , int page
        , int amount
) {

    public AdminPageDTO(String keyword, int page) {
        this(
                keyword == null ? null : "%" + keyword + "%"
                , page
                , 20
        );
    }
}
