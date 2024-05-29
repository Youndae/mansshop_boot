package com.example.mansshop_boot.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@EqualsAndHashCode
public class Member {

    @Id
    private String userId;

    private String userPw;

    private String userName;

    private String nickname;

    private String userEmail;

    private String provider;

    private Long memberPoint;

    private Date createdAt;

    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final List<Auth> auths = new ArrayList<>();

    public void addMemberAuth(Auth auth) {
        auths.add(auth);
        auth.setMember(this);
    }

    public Member(String userId
                , String userPw
                , String userName
                , String nickname
                , String userEmail
                , String provider
                , Long memberPoint
                , Date createdAt) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        this.userId = userId;
        this.userPw = userPw == null ? null : passwordEncoder.encode(userPw);
        this.userName = userName;
        this.nickname = nickname;
        this.userEmail = userEmail;
        this.provider = provider == null ? "local" : provider;
        this.memberPoint = memberPoint == null ? 0 : memberPoint;
        this.createdAt = createdAt;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
