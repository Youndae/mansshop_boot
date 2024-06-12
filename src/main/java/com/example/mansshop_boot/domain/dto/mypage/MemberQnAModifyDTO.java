package com.example.mansshop_boot.domain.dto.mypage;

public record MemberQnAModifyDTO(
        long qnaId
        , String title
        , String content
        , long classificationId
) {
}
