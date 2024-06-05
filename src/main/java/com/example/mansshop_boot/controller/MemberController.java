package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.domain.dto.member.JoinDTO;
import com.example.mansshop_boot.domain.dto.member.LoginDTO;
import com.example.mansshop_boot.domain.dto.member.LogoutDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseUserStatusDTO;
import com.example.mansshop_boot.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;

@RequestMapping("/api/member")
@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    @Value("#{jwt['token.access.header']}")
    private String authorizationHeader;

    @Value("#{jwt['cookie.ino.header']}")
    private String inoHeader;

    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<ResponseUserStatusDTO> loginProc(@RequestBody LoginDTO loginDTO
                                        , HttpServletRequest request
                                        , HttpServletResponse response){

        return memberService.loginProc(loginDTO, request, response);
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<?> logoutProc(HttpServletRequest request
                                        , HttpServletResponse response
                                        , Principal principal) {

        try{
            LogoutDTO dto = LogoutDTO.builder()
                    .authorizationToken(request.getHeader(authorizationHeader))
                    .inoValue(WebUtils.getCookie(request, inoHeader).getValue())
                    .userId(principal.getName())
                    .build();

            return memberService.logoutProc(dto, response);
        }catch (Exception e) {
            log.info("logout createDTO Exception");
            e.printStackTrace();
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());
        }


    }

    @PostMapping("/join")
    public ResponseEntity<?> joinProc(@RequestBody JoinDTO joinDTO) {


        log.info("member join :: joinDTO : {}", joinDTO);

        return memberService.joinProc(joinDTO);
    }



    @GetMapping("/oAuth/token")
    public ResponseEntity<Long> oAuthIssueToken(HttpServletRequest request, HttpServletResponse response) {


        return new ResponseEntity<>(memberService.oAuthUserIssueToken(request, response), HttpStatus.OK);
    }

    @GetMapping("/check-id")
    public ResponseEntity<?> checkJoinId(@RequestParam("userId") String userId) {

        return memberService.checkJoinId(userId);
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam("nickname") String nickname, Principal principal) {

        return memberService.checkNickname(nickname, principal);
    }

    @GetMapping("/check-login")
    public ResponseEntity<Boolean> checkLoginStatus(Principal principal) {

        return ResponseEntity.status(HttpStatus.OK)
                            .body(principal != null);
    }

}
