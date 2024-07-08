package com.example.mansshop_boot.domain.dto.member;

import lombok.Builder;

@Builder
public record LogoutDTO(
        String authorizationToken
        , String inoValue
        , String userId
) {
}
