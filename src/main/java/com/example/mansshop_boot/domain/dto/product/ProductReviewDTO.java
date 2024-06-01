package com.example.mansshop_boot.domain.dto.product;

import java.util.Date;

public record ProductReviewDTO(
        String reviewWriter
        , String reviewContent
        , Date reviewCreatedAt
        , String answerContent
        , Date answerCreatedAt
) {
}
