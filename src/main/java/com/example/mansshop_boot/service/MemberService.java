package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.member.business.LogoutDTO;
import com.example.mansshop_boot.domain.dto.member.business.UserSearchDTO;
import com.example.mansshop_boot.domain.dto.member.business.UserSearchPwDTO;
import com.example.mansshop_boot.domain.dto.member.in.JoinDTO;
import com.example.mansshop_boot.domain.dto.member.in.LoginDTO;
import com.example.mansshop_boot.domain.dto.member.in.UserCertificationDTO;
import com.example.mansshop_boot.domain.dto.member.in.UserResetPwDTO;
import com.example.mansshop_boot.domain.dto.member.out.UserSearchIdResponseDTO;
import com.example.mansshop_boot.domain.dto.member.out.UserStatusResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface MemberService {

    String joinProc(JoinDTO joinDTO);

    UserStatusResponseDTO loginProc(LoginDTO dto, HttpServletRequest request, HttpServletResponse response);

    String oAuthUserIssueToken(HttpServletRequest request, HttpServletResponse response);

    String checkJoinId(String userId);

    String checkNickname(String nickname, Principal principal);

    String logoutProc(LogoutDTO dto, HttpServletResponse response);

    UserSearchIdResponseDTO searchId(UserSearchDTO searchDTO);

    String searchPw(UserSearchPwDTO searchDTO);

    String checkCertificationNo(UserCertificationDTO certificationDTO);

    String resetPw(UserResetPwDTO resetDTO);
}
