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
            Member member = createMember(userId);
            Auth auth = createAuthByAuth(userId, MEMBER_AUTH);
            member.addMemberAuth(auth);

            memberList.add(member);
            authList.add(auth);
        }

        return new MemberAndAuthFixtureDTO(memberList, authList);
    }

    public static MemberAndAuthFixtureDTO createGoogleOAuth() {
        String userId = "googleOAuthUser";
        Member member = createMember(userId, "google");
        Auth auth = createAuthByAuth(userId, MEMBER_AUTH);
        member.addMemberAuth(auth);
        List<Member> memberList = List.of(member);
        List<Auth> auths = List.of(auth);

        return new MemberAndAuthFixtureDTO(memberList, auths);
    }

    public static MemberAndAuthFixtureDTO createAdmin() {
        String userId = "admin";
        Member member = createMember(userId);
        List<Auth> auths = List.of(
                createAuthByAuth(userId, MEMBER_AUTH),
                createAuthByAuth(userId, MANAGER_AUTH),
                createAuthByAuth(userId, ADMIN_AUTH)
        );
        auths.forEach(member::addMemberAuth);
        List<Member> admin = List.of(member);

        return new MemberAndAuthFixtureDTO(admin, auths);
    }

    public static MemberAndAuthFixtureDTO createAnonymous() {
        String userId = "Anonymous";
        Member member = createMember(userId);
        Auth auth = createAuthByAuth(userId, MEMBER_AUTH);
        member.addMemberAuth(auth);
        List<Member> anonymous = List.of(member);
        List<Auth> auths = List.of(auth);

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
                .phone("01012341234")
                .birth(LocalDate.now())
                .build();
    }

    private static Member createMember(String userId, String provider) {
        return Member.builder()
                .userId(userId)
                .userPw("1234")
                .userName(userId + "Name")
                .nickname(userId + "nickname")
                .userEmail(userId + "@" + userId + ".com")
                .provider(provider)
                .phone("01012341234")
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
