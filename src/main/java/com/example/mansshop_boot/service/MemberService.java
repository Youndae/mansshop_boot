package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.member.LoginDTO;
import com.example.mansshop_boot.domain.entity.Member;
import org.springframework.http.ResponseEntity;

public interface MemberService {

    public void joinProc(Member member);

    ResponseEntity<?> loginProc(LoginDTO dto);
}
