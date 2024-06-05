package com.example.mansshop_boot.domain.dto.member;

import com.example.mansshop_boot.domain.entity.Member;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Arrays;

@Builder
public record JoinDTO(
        String userId
        , String userPw
        , String userName
        , String nickname
        , String phone
        , String birth
        , String userEmail
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
