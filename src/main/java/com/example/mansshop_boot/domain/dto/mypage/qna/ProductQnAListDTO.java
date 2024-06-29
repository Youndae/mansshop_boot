package com.example.mansshop_boot.domain.dto.mypage.qna;

import java.time.LocalDate;

public record ProductQnAListDTO(
        Long productQnAId
        , String productName
        , boolean productQnAStat
        , LocalDate createdAt
) {
}
