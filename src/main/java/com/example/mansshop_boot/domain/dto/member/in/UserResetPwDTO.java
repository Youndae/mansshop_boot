package com.example.mansshop_boot.domain.dto.member.in;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "비밀번호 찾기 이후 비밀번호 재설정 요청 데이터")
public record UserResetPwDTO(
        @Schema(name = "userId", description = "사용자 아이디")
        String userId,
        @Schema(name = "certification", description = "인증번호")
        String certification,
        @Schema(name = "userPw", description = "새롭게 설정할 비밀번호")
        String userPw
) {
}
