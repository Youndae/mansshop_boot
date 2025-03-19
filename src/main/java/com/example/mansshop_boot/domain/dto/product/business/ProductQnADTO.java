package com.example.mansshop_boot.domain.dto.product.business;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


public record ProductQnADTO(
        Long qnaId
        , String writer
        , String qnaContent
        , LocalDate createdAt
        , boolean productQnAStat
) {

    public ProductQnADTO(Long qnaId, String writer, String qnaContent, LocalDateTime createdAt, boolean productQnAStat) {
        this(
                qnaId,
                writer,
                qnaContent,
                createdAt.toLocalDate(),
                productQnAStat
        );
    }
}
