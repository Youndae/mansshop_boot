package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.config.customException.exception.CustomBadCredentialsException;
import com.example.mansshop_boot.domain.dto.member.*;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseUserStatusDTO;
import com.example.mansshop_boot.domain.enumuration.Result;
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

            String responseMessage = memberService.logoutProc(dto, response);



            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseMessageDTO(responseMessage));
        }catch (Exception e) {
            log.info("logout createDTO Exception");
            e.printStackTrace();
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());
        }


    }

    @PostMapping("/join")
    public ResponseEntity<?> joinProc(@RequestBody JoinDTO joinDTO) {

        String responseMessage = memberService.joinProc(joinDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }



    @GetMapping("/oAuth/token")
    public ResponseEntity<?> oAuthIssueToken(HttpServletRequest request, HttpServletResponse response) {


        return memberService.oAuthUserIssueToken(request, response);
    }

    @GetMapping("/check-id")
    public ResponseEntity<?> checkJoinId(@RequestParam("userId") String userId) {

        String responseMessage = memberService.checkJoinId(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam("nickname") String nickname, Principal principal) {

        String responseMessage = memberService.checkNickname(nickname, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    @GetMapping("/check-login")
    public ResponseEntity<Boolean> checkLoginStatus(Principal principal) {

        return ResponseEntity.status(HttpStatus.OK)
                            .body(principal != null);
    }


    @GetMapping("/search-id")
    public ResponseEntity<UserSearchIdResponseDTO> searchId(@RequestParam(name = "userName") String userName
                                    , @RequestParam(name = "userPhone", required = false) String userPhone
                                    , @RequestParam(name = "userEmail", required = false) String userEmail) {

        UserSearchDTO searchDTO = new UserSearchDTO(userName, userPhone, userEmail);

        UserSearchIdResponseDTO responseDTO = memberService.searchId(searchDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @GetMapping("/search-pw")
    public ResponseEntity<ResponseMessageDTO> searchPw(@RequestParam(name = "id") String userId
                                                        , @RequestParam(name = "name") String userName
                                                        , @RequestParam(name = "email") String userEmail) {

        UserSearchPwDTO searchDTO = new UserSearchPwDTO(userId, userName, userEmail);

        String responseMessage = memberService.searchPw(searchDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    @PostMapping("/certification")
    public ResponseEntity<ResponseMessageDTO> checkCertification(@RequestBody UserCertificationDTO certificationDTO) {

        String responseMessage = memberService.checkCertificationNo(certificationDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    @PatchMapping("/reset-pw")
    public ResponseEntity<ResponseMessageDTO> resetPassword(@RequestBody UserResetPwDTO resetDTO) {
        log.info("MemberController.resetPassword :: dto : {}", resetDTO);


        String responseMessage = memberService.resetPw(resetDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }
}
