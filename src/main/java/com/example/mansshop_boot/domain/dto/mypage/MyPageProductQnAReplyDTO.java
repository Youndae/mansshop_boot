package com.example.mansshop_boot.domain.dto.mypage;

import java.time.LocalDate;

public record MyPageProductQnAReplyDTO(
        long replyId
        , String writer
        , String replyContent
        , LocalDate updatedAt
) {
}
