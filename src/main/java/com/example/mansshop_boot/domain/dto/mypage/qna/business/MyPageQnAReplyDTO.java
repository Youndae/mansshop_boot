package com.example.mansshop_boot.domain.dto.mypage.qna.business;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MyPageQnAReplyDTO(
        long replyId,
        String writer,
        String replyContent,
        LocalDate updatedAt
) {

    public MyPageQnAReplyDTO(long replyId, String writer, String replyContent, LocalDateTime updatedAt) {
        this(
                replyId,
                writer,
                replyContent,
                updatedAt.toLocalDate()
        );
    }
}
