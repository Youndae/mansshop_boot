package com.example.mansshop_boot.domain.dto.product;

import java.util.Date;

public record ProductReviewDTO(
        String writer
        , String reviewContent
        , Date createdAt
        , int reviewStep
) {
}
