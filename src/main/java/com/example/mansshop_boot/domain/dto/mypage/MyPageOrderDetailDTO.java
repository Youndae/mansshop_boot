package com.example.mansshop_boot.domain.dto.mypage;

public record MyPageOrderDetailDTO(
        long orderId
        , String productId
        , long optionId
        , long detailId
        , String productName
        , String size
        , String color
        , int detailCount
        , int detailPrice
        , boolean reviewStatus
        , String thumbnail
) {
}
