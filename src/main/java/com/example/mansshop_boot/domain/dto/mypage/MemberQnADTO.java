package com.example.mansshop_boot.domain.dto.mypage;

import java.time.LocalDate;

public record MemberQnADTO(
        long memberQnAId
        , String qnaClassification
        , String qnaTitle
        , String writer
        , String qnaContent
        , LocalDate updatedAt
        , int memberQnAStat
) {
}
