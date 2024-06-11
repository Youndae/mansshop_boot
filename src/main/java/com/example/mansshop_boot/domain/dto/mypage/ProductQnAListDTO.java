package com.example.mansshop_boot.domain.dto.mypage;

import java.time.LocalDate;

public record ProductQnAListDTO(
        Long productQnAId
        , String productName
        , int productQnAStat
        , LocalDate createdAt
) {
}
