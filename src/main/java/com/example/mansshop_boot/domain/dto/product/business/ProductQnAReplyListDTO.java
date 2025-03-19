package com.example.mansshop_boot.domain.dto.product.business;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProductQnAReplyListDTO(
        String writer,
        String replyContent,
        Long qnaId,
        LocalDateTime createdAt
) {
}
