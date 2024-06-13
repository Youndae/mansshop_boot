package com.example.mansshop_boot.domain.dto.mypage.qna;

import lombok.Builder;

@Builder
public record QnAClassificationDTO(
        long id
        , String name
) {
}
