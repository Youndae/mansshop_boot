package com.example.mansshop_boot.domain.dto.product;

import com.example.mansshop_boot.domain.entity.Product;
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

    public ProductDetailDTO(Product product
                            , boolean likeStat
                            , List<ProductOptionDTO> productOptionList
                            , List<String> productThumbnailList
                            , List<String> productInfoImageList
                            , ProductPageableDTO<ProductReviewDTO> productReviewList
                            , ProductPageableDTO<ProductQnAResponseDTO> productQnAList) {
        this(
                product.getId()
                , product.getProductName()
                , product.getProductPrice()
                , product.getThumbnail()
                , likeStat
                , product.getProductDiscount()
                , (int) (product.getProductPrice() * (1 - ((double) product.getProductDiscount() / 100)))
                , productOptionList
                , productThumbnailList
                , productInfoImageList
                , productReviewList
                , productQnAList
        );
    }
}
