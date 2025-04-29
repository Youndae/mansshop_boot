package com.example.mansshop_boot.domain.entity;

import com.example.mansshop_boot.domain.enumeration.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "auth")
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Member member;

    @Column(length = 50,
            nullable = false
    )
    private String auth;

    public void setMember(Member member) {
        this.member = member;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public Auth(Member member) {
        this.member = member;
        this.auth = Role.MEMBER.getKey();
    }

    public Auth(Member member, String auth) {
        this.member = member;
        this.auth = auth;
    }
}
