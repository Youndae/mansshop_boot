package com.example.mansshop_boot.domain.dto.product;

import java.time.LocalDate;
import java.util.Date;

public record ProductReviewDTO(
        String reviewWriter
        , String reviewContent
        , LocalDate reviewCreatedAt
        , String answerContent
        , LocalDate answerCreatedAt
) {
}
