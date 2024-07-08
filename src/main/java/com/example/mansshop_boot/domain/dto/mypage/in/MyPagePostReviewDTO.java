package com.example.mansshop_boot.domain.dto.mypage.in;

public record MyPagePostReviewDTO(
        String productId
        , String content
        , long optionId
        , long detailId
) {
}
