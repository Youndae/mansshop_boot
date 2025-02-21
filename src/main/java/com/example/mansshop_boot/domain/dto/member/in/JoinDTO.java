package com.example.mansshop_boot.domain.dto.member.in;

import com.example.mansshop_boot.domain.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Arrays;

@Schema(name = "회원가입 요청 데이터")
@Builder
public record JoinDTO(
        @Schema(name = "userId", description = "사용자 아이디")
        @NotNull(message = "아이디는 필수 사항입니다.")
        String userId,
        @Schema(name = "userPw", description = "사용자 비밀번호")
        @NotNull(message = "비밀번호는 필수 사항입니다.")
        String userPw,
        @Schema(name = "userName", description = "사용자 이름")
        @NotNull(message = "사용자 이름은 필수 사항입니다.")
        String userName,
        @Schema(name = "nickname", description = "사용자 닉네임")
        String nickname,
        @Schema(name = "phone", description = "사용자 연락처", example = "01012345678")
        @NotNull(message = "연락처는 필수 사항입니다.")
        String phone,
        @Schema(name = "birth", description = "사용자 생년월일", example = "2015/02/09")
        @NotNull(message = "생년월일은 필수 사항입니다.")
        String birth,
        @Schema(name = "userEmail", description = "사용자 이메일")
        @NotNull(message = "이메일은 필수 사항입니다.")
        String userEmail
) {

    public Member toEntity() {
        int[] splitBirth = Arrays.stream(birth.split("/")).mapToInt(Integer::parseInt).toArray();

        LocalDate birth = LocalDate.of(splitBirth[0], splitBirth[1], splitBirth[2]);

        return Member.builder()
                .userId(userId)
                .userPw(userPw)
                .userName(userName)
                .nickname(nickname)
                .phone(phone)
                .birth(birth)
                .userEmail(userEmail)
                .provider("local")
                .build();
    }
}
