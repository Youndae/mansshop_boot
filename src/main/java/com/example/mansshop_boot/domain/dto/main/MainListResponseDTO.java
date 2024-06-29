package com.example.mansshop_boot.domain.dto.main;

public record MainListResponseDTO(
        String productId
        , String productName
        , String thumbnail
        , int originPrice
        , int discount
        , int discountPrice
        , boolean isSoldOut
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
