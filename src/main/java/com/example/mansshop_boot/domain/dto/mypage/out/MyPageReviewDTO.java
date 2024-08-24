package com.example.mansshop_boot.domain.dto.mypage.out;

import java.time.LocalDate;

public record MyPageReviewDTO(
        long reviewId
        , String thumbnail
        , String productName
        , String content
        , LocalDate createdAt
        , LocalDate updatedAt
        , String replyContent
        , LocalDate replyUpdatedAt
) {
}
