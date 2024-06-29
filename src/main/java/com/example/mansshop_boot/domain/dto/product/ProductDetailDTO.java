package com.example.mansshop_boot.domain.dto.product;

import com.example.mansshop_boot.domain.dto.member.UserStatusDTO;
import lombok.Builder;
import org.springframework.data.domain.Page;

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
        , UserStatusDTO userStatus
){
}
