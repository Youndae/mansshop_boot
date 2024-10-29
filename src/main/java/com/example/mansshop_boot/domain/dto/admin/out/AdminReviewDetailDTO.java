package com.example.mansshop_boot.domain.dto.admin.out;

import java.time.LocalDate;

public record AdminReviewDetailDTO(
        long reviewId
        , String productName
        , String size
        , String color
        , String writer
        , LocalDate createdAt
        , LocalDate updatedAt
        , String content
        , LocalDate replyUpdatedAt
        , String replyContent
) {
}
