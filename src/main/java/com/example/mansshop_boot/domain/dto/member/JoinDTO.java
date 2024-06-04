package com.example.mansshop_boot.domain.dto.member;

import com.example.mansshop_boot.domain.entity.Member;
import lombok.Builder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

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
        Calendar cal = Calendar.getInstance();
        cal.set(splitBirth[0], splitBirth[1] - 1, splitBirth[2]);



        return Member.builder()
                .userId(userId)
                .userPw(userPw)
                .userName(userName)
                .nickname(nickname)
                .phone(phone)
                .birth(new Date(cal.getTimeInMillis()))
                .userEmail(userEmail)
                .provider("local")
                .build();
    }
}
