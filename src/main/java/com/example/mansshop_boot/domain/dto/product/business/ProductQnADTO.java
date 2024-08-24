package com.example.mansshop_boot.domain.dto.product.business;

import java.time.LocalDate;
import java.util.Date;


public record ProductQnADTO(
        Long qnaId
        , String writer
        , String qnaContent
        , LocalDate createdAt
        , boolean productQnAStat
) {
}
