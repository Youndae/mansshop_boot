package com.example.mansshop_boot.domain.dto.mypage;


import java.time.LocalDate;

public record MemberQnAListDTO(
        long memberQnAId
        , String memberQnATitle
        , int memberQnAStat
        , String qnaClassification
        , LocalDate updatedAt
) {
}
