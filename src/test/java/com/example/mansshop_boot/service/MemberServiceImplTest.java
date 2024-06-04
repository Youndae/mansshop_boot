package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.member.JoinDTO;
import com.example.mansshop_boot.domain.entity.Auth;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberServiceImplTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("MemberService.joinProc 테스트")
    @Transactional
    void joinTest() {

        JoinDTO joinDTO = JoinDTO.builder()
                .userId("tester")
                .userPw("1234")
                .userName("테스터")
                .nickname("테스터입니다")
                .phone("01056781234")
                .birth("2016/02/09")
                .userEmail("tester@tester.com")
                .build();

        memberService.joinProc(joinDTO);

        Member member = memberRepository.findById("tester").orElse(null);

        System.out.println("result : " + member);
    }
}