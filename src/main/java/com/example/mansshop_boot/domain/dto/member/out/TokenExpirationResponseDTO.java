package com.example.mansshop_boot.domain.dto.member.out;

import java.time.Instant;

public record TokenExpirationResponseDTO(
        Instant expiration
) {
}
