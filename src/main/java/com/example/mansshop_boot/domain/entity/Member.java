package com.example.mansshop_boot.domain.entity;

import com.example.mansshop_boot.domain.dto.mypage.in.MyPageInfoPatchDTO;
import com.example.mansshop_boot.auth.oAuth.response.OAuth2DTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Table(name = "member")
public class Member {

    @Id
    @Column(length = 50)
    private String userId;

    @Column(length = 200)
    private String userPw;

    @Column(length = 100,
            nullable = false
    )
    private String userName;

    @Column(length = 100)
    private String nickname;

    @Column(length = 100,
            nullable = false
    )
    private String userEmail;

    @Column(length = 20,
            nullable = false
    )
    private String provider;

    @Column(nullable = false,
            columnDefinition = "INT DEFAULT 0"
    )
    private Long memberPoint;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDate createdAt;

    @Column(length = 20)
    private String phone;

    private LocalDate birth;

    @OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
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
                , LocalDate birth) {
        String phoneRegEx = "(\\d{3})(\\d{3,4})(\\d{4})";

        this.userId = userId;
        this.userPw = userPw == null ? null : encodePw(userPw);
        this.userName = userName;
        this.nickname = nickname;
        this.userEmail = userEmail;
        this.provider = provider == null ? "local" : provider;
        this.memberPoint = memberPoint == null ? 0 : memberPoint;
        this.phone = phone == null ? null : phone.replaceAll(phoneRegEx, "$1-$2-$3");
        this.birth = birth;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserPw(String userPw) {
        this.userPw = encodePw(userPw);
    }

    public void setMemberPoint(Long memberPoint) {
        this.memberPoint = memberPoint;
    }

    private String encodePw(String userPw) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        return passwordEncoder.encode(userPw);
    }

    public void patchUser(MyPageInfoPatchDTO patchDTO) {
        String phoneRegEx = "(\\d{3})(\\d{3,4})(\\d{4})";

        this.nickname = patchDTO.nickname();
        this.phone = patchDTO.phone().replaceAll(phoneRegEx, "$1-$2-$3");
        this.userEmail = patchDTO.mail();
    }

    public OAuth2DTO toOAuth2DTOUseFilter() {
        return new OAuth2DTO(
                this.userId
                , this.userName
                , this.auths
                , null
        );
    }
}
