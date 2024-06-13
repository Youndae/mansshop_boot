package com.example.mansshop_boot.domain.dto.mypage.qna.req;

public record MemberQnAModifyDTO(
        long qnaId
        , String title
        , String content
        , long classificationId
) {
}
