package com.example.mansshop_boot.domain.dto.mypage;

import lombok.Builder;

@Builder
public record MyPageInfoDTO(
        String nickname
        , String phone
        , String mailPrefix
        , String mailSuffix
        , String mailType
) {
}
