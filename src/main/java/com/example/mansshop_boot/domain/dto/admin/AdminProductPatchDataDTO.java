package com.example.mansshop_boot.domain.dto.admin;

import com.example.mansshop_boot.domain.dto.member.UserStatusDTO;
import com.example.mansshop_boot.domain.entity.Product;
import lombok.Builder;

import java.util.List;

public record AdminProductPatchDataDTO(
        String productId
        , String productName
        , String classificationId
        , String firstThumbnail
        , int price
        , boolean isOpen
        , int discount
        , List<AdminProductOptionDTO> optionList
        , List<String> thumbnailList
        , List<String> infoImageList
        , List<String> classificationList
        , UserStatusDTO userStatus
) {

    @Builder
    public AdminProductPatchDataDTO(Product product
                                    , List<AdminProductOptionDTO> optionList
                                    , List<String> thumbnailList
                                    , List<String> infoImageList
                                    , List<String> classificationList
                                    , String nickname) {
        this(
                product.getId()
                , product.getProductName()
                , product.getClassification().getId()
                , product.getThumbnail()
                , product.getProductPrice()
                , product.isOpen()
                , product.getProductDiscount()
                , optionList
                , thumbnailList
                , infoImageList
                , classificationList
                , new UserStatusDTO(nickname)
        );
    }
}
