package com.example.mansshop_boot.Fixture.domain.member;

import com.example.mansshop_boot.domain.entity.Auth;
import com.example.mansshop_boot.domain.entity.Member;

import java.util.List;

public record MemberAndAuthFixtureDTO(
        List<Member> memberList,
        List<Auth> authList
) {
}
