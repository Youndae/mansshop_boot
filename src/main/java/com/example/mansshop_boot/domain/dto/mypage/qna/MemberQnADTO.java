package com.example.mansshop_boot.domain.dto.mypage.qna;

import java.time.LocalDate;

public record MemberQnADTO(
        long memberQnAId
        , String qnaClassification
        , String qnaTitle
        , String writer
        , String qnaContent
        , LocalDate updatedAt
        , boolean memberQnAStat
) {
}
