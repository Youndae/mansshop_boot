package com.example.mansshop_boot.domain.dto.mypage;

public record MyPageOrderDetailDTO(
        long orderId
        , long detailId
        , String productName
        , String size
        , String color
        , int detailCount
        , int detailPrice
        , int reviewStatus
        , String thumbnail
) {
}
