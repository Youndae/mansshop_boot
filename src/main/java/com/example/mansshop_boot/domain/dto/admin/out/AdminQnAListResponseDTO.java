package com.example.mansshop_boot.domain.dto.admin.out;

import java.time.LocalDate;

public record AdminQnAListResponseDTO(
        long qnaId
        , String classification
        , String title
        , String writer
        , LocalDate createdAt
        , boolean answerStatus
) {
}
