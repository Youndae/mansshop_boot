package com.example.mansshop_boot.domain.dto.member.business;

public record UserSearchPwDTO(
        String userId,
        String userName,
        String userEmail
) {
}
