package com.example.mansshop_boot.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Member {

    @Id
    private String userId;

    private String userPw;

    private String userName;

    private String nickname;

    private String userEmail;

    private String provider;

    private Long memberPoint;

    @CreationTimestamp
    private Date createdAt;

    private String phone;

    private Date birth;

    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final List<Auth> auths = new ArrayList<>();

    public void addMemberAuth(Auth auth) {
        auths.add(auth);
        auth.setMember(this);
    }

    @Builder
    public Member(String userId
                , String userPw
                , String userName
                , String nickname
                , String userEmail
                , String provider
                , Long memberPoint
                , String phone
                , Date birth) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String phoneRegEx = "(\\d{3})(\\d{3,4})(\\d{4})";

        this.userId = userId;
        this.userPw = userPw == null ? null : passwordEncoder.encode(userPw);
        this.userName = userName;
        this.nickname = nickname;
        this.userEmail = userEmail;
        this.provider = provider == null ? "local" : provider;
        this.memberPoint = memberPoint == null ? 0 : memberPoint;
        this.phone = phone.replaceAll(phoneRegEx, "$1-$2-$3");
        this.birth = birth;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
