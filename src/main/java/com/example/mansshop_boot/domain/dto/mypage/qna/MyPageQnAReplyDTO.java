package com.example.mansshop_boot.domain.dto.mypage.qna;

import java.time.LocalDate;

public record MyPageQnAReplyDTO(
        long replyId
        , String writer
        , String replyContent
        , LocalDate updatedAt
) {
}
