package com.example.mansshop_boot.domain.dto.product;

import lombok.Builder;

import java.util.List;

@Builder
public record ProductDetailDTO(
        String productId
        , String productName
        , int productPrice
        , String productImageName
        , boolean likeStat
        , int discount
        , int discountPrice
        , List<ProductOptionDTO> productOptionList
        , List<String> productThumbnailList
        , List<String> productInfoImageList
        , ProductPageableDTO<ProductReviewDTO> productReviewList
        , ProductPageableDTO<ProductQnAResponseDTO> productQnAList
){
}
