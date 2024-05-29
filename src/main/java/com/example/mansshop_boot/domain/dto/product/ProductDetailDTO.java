package com.example.mansshop_boot.domain.dto.product;

import com.example.mansshop_boot.domain.dto.member.UserStatusDTO;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record ProductDetailDTO(
        String productId
        , String productName
        , long productPrice
        , String productImageName
        , List<ProductOptionDTO> productOptionList
        , List<String> productThumbnailList
        , List<String> productInfoImageList
        , Page<ProductReviewDTO> productReviewList
        , Page<ProductQnADTO> productQnAList
        , UserStatusDTO userStatus
){
}
