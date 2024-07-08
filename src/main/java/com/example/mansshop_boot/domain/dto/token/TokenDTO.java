package com.example.mansshop_boot.domain.dto.token;

import lombok.Builder;

@Builder
public record TokenDTO(
        String accessTokenValue
        , String refreshTokenValue
        , String inoValue
) {
}
