package com.example.mansshop_boot.domain.entity;

import com.example.mansshop_boot.domain.enumuration.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @JoinColumn(name = "userId", nullable = false)
    private Member member;

    @Column(length = 50,
            nullable = false
    )
    private String auth;

    public void setMember(Member member) {
        this.member = member;
    }

    public Auth toMemberAuth() {
        return Auth.builder()
                .auth(Role.MEMBER.getKey())
                .build();
    }
}
