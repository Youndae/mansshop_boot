package com.example.mansshop_boot.domain.dto.mypage.qna.req;

public record MemberQnAInsertDTO(
        String title
        , String content
        , long classificationId
) {
}
