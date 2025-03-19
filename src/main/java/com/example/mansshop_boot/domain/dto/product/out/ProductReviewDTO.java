package com.example.mansshop_boot.domain.dto.product.out;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public record ProductReviewDTO(
        String reviewWriter
        , String reviewContent
        , LocalDate reviewCreatedAt
        , String answerContent
        , LocalDate answerCreatedAt
) {

    public ProductReviewDTO(String reviewWriter,
                            String reviewContent,
                            LocalDateTime reviewCreatedAt,
                            String answerContent,
                            LocalDateTime answerCreatedAt) {
        this(
                reviewWriter,
                reviewContent,
                reviewCreatedAt.toLocalDate(),
                answerContent,
                answerCreatedAt.toLocalDate()
        );
    }
}
