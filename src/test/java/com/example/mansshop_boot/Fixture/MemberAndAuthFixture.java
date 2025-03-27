package com.example.mansshop_boot.Fixture;

import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.domain.entity.Auth;
import com.example.mansshop_boot.domain.entity.Member;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MemberAndAuthFixture {

    private static final String MEMBER_AUTH = "ROLE_MEMBER";

    private static final String MANAGER_AUTH = "ROLE_MANAGER";

    private static final String ADMIN_AUTH = "ROLE_ADMIN";

    public static MemberAndAuthFixtureDTO createDefaultMember (int count) {
        List<Member> memberList = new ArrayList<>();
        List<Auth> authList = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            String userId = "tester" + i;
            memberList.add(createMember(userId));
            authList.add(createAuthByAuth(userId, MEMBER_AUTH));
        }

        return new MemberAndAuthFixtureDTO(memberList, authList);
    }

    public static MemberAndAuthFixtureDTO createAdmin() {
        String userId = "admin";
        List<Member> admin = List.of(createMember(userId));
        List<Auth> auths = List.of(
                createAuthByAuth(userId, MEMBER_AUTH),
                createAuthByAuth(userId, MANAGER_AUTH),
                createAuthByAuth(userId, ADMIN_AUTH)
        );

        return new MemberAndAuthFixtureDTO(admin, auths);
    }

    public static MemberAndAuthFixtureDTO createAnonymous() {
        String userId = "Anonymous";
        List<Member> anonymous = List.of(createMember(userId));
        List<Auth> auths = List.of(createAuthByAuth(userId, MEMBER_AUTH));

        return new MemberAndAuthFixtureDTO(anonymous, auths);
    }


    private static Member createMember(String userId) {
        return Member.builder()
                .userId(userId)
                .userPw("1234")
                .userName(userId + "Name")
                .nickname(userId + "nickname")
                .userEmail(userId + "@" + userId + ".com")
                .provider("local")
                .phone("010-1234-1234")
                .birth(LocalDate.now())
                .build();
    }

    private static Auth createAuthByAuth(String userId, String auth) {
        return Auth.builder()
                .member(Member.builder().userId(userId).build())
                .auth(auth)
                .build();
    }
}
