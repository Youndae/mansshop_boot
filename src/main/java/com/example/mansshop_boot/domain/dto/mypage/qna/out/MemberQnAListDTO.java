package com.example.mansshop_boot.domain.dto.mypage.qna.out;


import java.time.LocalDate;

public record MemberQnAListDTO(
        long memberQnAId
        , String memberQnATitle
        , boolean memberQnAStat
        , String qnaClassification
        , LocalDate updatedAt
) {
}
