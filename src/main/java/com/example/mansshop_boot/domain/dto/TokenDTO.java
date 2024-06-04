package com.example.mansshop_boot.domain.dto;

import lombok.Builder;

@Builder
public record TokenDTO(
        String accessTokenValue
        , String refreshTokenValue
        , String inoValue
) {
}
