package com.example.mansshop_boot.domain.dto.member.in;

public record UserResetPwDTO(
        String userId
        , String certification
        , String userPw
) {
}
