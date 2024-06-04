package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.member.JoinDTO;
import com.example.mansshop_boot.domain.entity.Auth;
import com.example.mansshop_boot.domain.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("로컬 가입 테스트")
    @Transactional
    void joinTest() {

        JoinDTO joinDTO = JoinDTO.builder()
                .userId("tester123")
                .userPw("1234")
                .userName("테스터")
                .nickname("테스터입니다")
                .phone("01056781234")
                .birth("2016/02/09")
                .userEmail("tester@tester.com")
                .build();

        Member member = joinDTO.toEntity();
        member.addMemberAuth(new Auth().toMemberAuth());

        System.out.println("member birth : " + member.getBirth());

        memberRepository.save(member);

        Member existsMember = memberRepository.findById("tester").orElse(null);

        System.out.println("result : " + existsMember);
    }
}