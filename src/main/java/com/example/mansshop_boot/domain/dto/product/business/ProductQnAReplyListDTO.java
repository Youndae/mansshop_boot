package com.example.mansshop_boot.domain.dto.product.business;

import java.time.LocalDate;

public record ProductQnAReplyListDTO(
        String writer,
        String replyContent,
        Long qnaId,
        LocalDate createdAt
) {
}
