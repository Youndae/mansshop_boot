package com.example.mansshop_boot.domain.dto.member.out;

public record LoginResponseDTO(
        UserStatusResponseDTO userStatus,
        TokenExpirationResponseDTO tokenExpiration
) {
}
