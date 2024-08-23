package com.example.mansshop_boot.domain.dto.admin;

import com.example.mansshop_boot.domain.entity.Product;
import lombok.Builder;

import java.util.List;

public record AdminProductDetailDTO(
        String productId
        , String classification
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

    /*@Builder
    public AdminProductDetailDTO(String productId
                                , String classification
                                , String productName
                                , String firstThumbnail
                                , List<String> thumbnailList
                                , List<String> infoImageList
                                , List<AdminProductOptionDTO> optionList
                                , int price
                                , boolean isOpen
                                , long sales
                                , int discount) {
        this.productId = productId;
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
    }*/

    public AdminProductDetailDTO (String productId
                                            , Product product
                                            , List<String> thumbnailList
                                            , List<String> infoImageList
                                            , List<AdminProductOptionDTO> productOptionList) {
        this(
                productId
                , product.getClassification().getId()
                , product.getProductName()
                , product.getThumbnail()
                , thumbnailList
                , infoImageList
                , productOptionList
                , product.getProductPrice()
                , product.isOpen()
                , product.getProductSales()
                , product.getProductDiscount()
        );
    }
}
