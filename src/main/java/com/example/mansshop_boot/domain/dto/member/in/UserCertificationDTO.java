package com.example.mansshop_boot.domain.dto.member.in;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "비밀번호 찾기 인증번호 요청 데이터")
public record UserCertificationDTO(

        @Schema(name = "userId", description = "사용자 아이디")
        String userId,
        @Schema(name = "certification", description = "인증 번호")
        String certification
) {
}
