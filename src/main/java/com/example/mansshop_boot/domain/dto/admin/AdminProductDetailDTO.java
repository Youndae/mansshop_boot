package com.example.mansshop_boot.domain.dto.admin;

import lombok.Builder;

import java.util.List;

public record AdminProductDetailDTO(
        String classification
        , String productName
        , String firstThumbnail
        , List<String> thumbnailList
        , List<String> infoImageList
        , List<AdminProductOptionDTO> optionList
        , int price
        , boolean isOpen
        , long sales
        , int discount
) {

    @Builder
    public AdminProductDetailDTO(String classification
                                , String productName
                                , String firstThumbnail
                                , List<String> thumbnailList
                                , List<String> infoImageList
                                , List<AdminProductOptionDTO> optionList
                                , int price
                                , boolean isOpen
                                , long sales
                                , int discount) {
        this.classification = classification;
        this.productName = productName;
        this.firstThumbnail = firstThumbnail;
        this.thumbnailList = thumbnailList;
        this.infoImageList = infoImageList;
        this.optionList = optionList;
        this.price = price;
        this.isOpen = isOpen;
        this.sales = sales;
        this.discount = discount;
    }
}
