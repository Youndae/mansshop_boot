package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.member.LoginDTO;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;

    @Override
    public void joinProc(Member member) {

        memberRepository.save(member);
    }

    @Override
    public ResponseEntity<?> loginProc(LoginDTO dto) {


        return null;
    }
}
