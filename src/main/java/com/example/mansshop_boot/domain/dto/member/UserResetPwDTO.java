package com.example.mansshop_boot.domain.dto.member;

public record UserResetPwDTO(
        String userId
        , String certification
        , String userPw
) {
}
