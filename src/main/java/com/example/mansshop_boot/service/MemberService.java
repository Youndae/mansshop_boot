package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.member.*;
import com.example.mansshop_boot.domain.dto.response.ResponseUserStatusDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface MemberService {

    String joinProc(JoinDTO joinDTO);

    ResponseEntity<ResponseUserStatusDTO> loginProc(LoginDTO dto, HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<?> oAuthUserIssueToken(HttpServletRequest request, HttpServletResponse response);

    String checkJoinId(String userId);

    String checkNickname(String nickname, Principal principal);

    String logoutProc(LogoutDTO dto, HttpServletResponse response);

    UserSearchIdResponseDTO searchId(UserSearchDTO searchDTO);

    String searchPw(UserSearchPwDTO searchDTO);

    String checkCertificationNo(UserCertificationDTO certificationDTO);

    String resetPw(UserResetPwDTO resetDTO);
}
