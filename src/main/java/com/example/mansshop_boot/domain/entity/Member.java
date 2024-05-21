package com.example.mansshop_boot.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;

@Entity
@Getter
@Builder
@NoArgsConstructor
public class Member {

    @Id
    private String userId;

    private String userPw;

    private String userName;

    private String nickname;

    private String userEmail;

    private String provider;

    private int memberPoint;

    private Date createdAt;

    public Member(String userId
                , String userPw
                , String userName
                , String nickname
                , String userEmail
                , String provider
                , int memberPoint
                , Date createdAt) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        this.userId = userId;
        this.userPw = passwordEncoder.encode(userPw);
        this.userName = userName;
        this.nickname = nickname;
        this.userEmail = userEmail;
        this.provider = provider;
        this.memberPoint = memberPoint;
        this.createdAt = createdAt;
    }
}
