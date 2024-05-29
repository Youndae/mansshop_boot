package com.example.mansshop_boot.domain.dto.member;

import com.example.mansshop_boot.domain.entity.Member;
import lombok.Builder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Builder
public record JoinDTO(
        String userId
        , String userPw
        , String userName
        , String nickName
        , String userEmail
) {

    public Member toEntity() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        return Member.builder()
                .userId(userId)
                .userPw(passwordEncoder.encode(userPw))
                .userName(userName)
                .nickname(nickName)
                .userEmail(userEmail)
                .provider("local")
                .build();
    }
}
