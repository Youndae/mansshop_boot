package com.example.mansshop_boot.domain.dto.mypage;

public record MemberQnAInsertDTO(
        String title
        , String content
        , long classificationId
) {
}
