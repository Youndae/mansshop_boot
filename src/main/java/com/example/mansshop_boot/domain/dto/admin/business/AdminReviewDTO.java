package com.example.mansshop_boot.domain.dto.admin.business;

import java.time.LocalDate;

public record AdminReviewDTO(
        long reviewId
        , String productName
        , String writer
        , LocalDate updatedAt
        , boolean status
) {
}
