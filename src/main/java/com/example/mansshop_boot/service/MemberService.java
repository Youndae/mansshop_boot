package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.member.JoinDTO;
import com.example.mansshop_boot.domain.dto.member.LoginDTO;
import com.example.mansshop_boot.domain.entity.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface MemberService {

    long joinProc(JoinDTO joinDTO);

    long loginProc(LoginDTO dto, HttpServletRequest request, HttpServletResponse response);

    long oAuthUserIssueToken(HttpServletRequest request, HttpServletResponse response);
}
