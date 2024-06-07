package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.member.JoinDTO;
import com.example.mansshop_boot.domain.dto.member.LoginDTO;
import com.example.mansshop_boot.domain.dto.member.LogoutDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseUserStatusDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface MemberService {

    ResponseEntity<?> joinProc(JoinDTO joinDTO);

    ResponseEntity<ResponseUserStatusDTO> loginProc(LoginDTO dto, HttpServletRequest request, HttpServletResponse response);

    long oAuthUserIssueToken(HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<?> checkJoinId(String userId);

    ResponseEntity<?> checkNickname(String nickname, Principal principal);

    ResponseEntity<?> logoutProc(LogoutDTO dto, HttpServletResponse response);

}
