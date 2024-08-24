package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.domain.dto.member.business.LogoutDTO;
import com.example.mansshop_boot.domain.dto.member.business.UserSearchDTO;
import com.example.mansshop_boot.domain.dto.member.business.UserSearchPwDTO;
import com.example.mansshop_boot.domain.dto.member.in.JoinDTO;
import com.example.mansshop_boot.domain.dto.member.in.LoginDTO;
import com.example.mansshop_boot.domain.dto.member.in.UserCertificationDTO;
import com.example.mansshop_boot.domain.dto.member.in.UserResetPwDTO;
import com.example.mansshop_boot.domain.dto.member.out.UserSearchIdResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseUserStatusDTO;
import com.example.mansshop_boot.domain.dto.response.UserStatusDTO;
import com.example.mansshop_boot.service.MemberService;
import com.example.mansshop_boot.service.PrincipalService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

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

    private final PrincipalService principalService;

    /**
     *
     * @param loginDTO
     * @param request
     * @param response
     *
     * 로컬 로그인 요청
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseUserStatusDTO> loginProc(@RequestBody LoginDTO loginDTO
                                        , HttpServletRequest request
                                        , HttpServletResponse response){

        return memberService.loginProc(loginDTO, request, response);
    }

    /**
     *
     * @param request
     * @param response
     * @param principal
     *
     * 로그아웃 요청
     */
    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ResponseMessageDTO> logoutProc(HttpServletRequest request
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

    /**
     *
     * @param joinDTO
     *
     * 회원 가입 요청
     */
    @PostMapping("/join")
    public ResponseEntity<?> joinProc(@RequestBody JoinDTO joinDTO) {

        String responseMessage = memberService.joinProc(joinDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }


    /**
     *
     * @param request
     * @param response
     *
     * OAuth2 로그인 사용자의 토큰 발급 요청.
     * OAuth2 로그인의 경우 href 요청으로 처리되기 때문에 쿠키 반환은 가능하나 응답 헤더에 Authorization을 담을 수 없다.
     * 그렇기 때문에 OAuth2 로그인 사용자에 대해서는 임시 토큰을 응답 쿠키에 담아 먼저 발급한 뒤
     * 특정 컴포넌트로 Redirect 되도록 처리했고 그 컴포넌트에서는 임시 토큰을 통해 정상적인 토큰의 발급을 요청한다.
     * 해당 요청이 여기로 오는 것.
     */
    @GetMapping("/oAuth/token")
    public ResponseEntity<?> oAuthIssueToken(HttpServletRequest request, HttpServletResponse response) {


        return memberService.oAuthUserIssueToken(request, response);
    }

    /**
     *
     * @param userId
     *
     * 회원 가입 중 아이디 중복 체크
     */
    @GetMapping("/check-id")
    public ResponseEntity<?> checkJoinId(@RequestParam("userId") String userId) {

        String responseMessage = memberService.checkJoinId(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param nickname
     * @param principal
     *
     * 회원가입 중 닉네임 중복 체크
     */
    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam("nickname") String nickname, Principal principal) {

        String responseMessage = memberService.checkNickname(nickname, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param principal
     *
     * 클라이언트에서 로그인 상태 체크 요청
     * 별다른 데이터는 필요하지 않으나 새로고침에 대한 Redux의 처리를 위함.
     */
    @GetMapping("/check-login")
    public ResponseEntity<ResponseUserStatusDTO> checkLoginStatus(Principal principal) {

        String nickname = null;

        if(principal != null)
            nickname = principalService.getNicknameByPrincipal(principal);

        return ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseUserStatusDTO(new UserStatusDTO(nickname)));
    }

    /**
     *
     * @param userName
     * @param userPhone
     * @param userEmail
     *
     * 아이디 찾기
     */
    @GetMapping("/search-id")
    public ResponseEntity<UserSearchIdResponseDTO> searchId(@RequestParam(name = "userName") String userName
                                    , @RequestParam(name = "userPhone", required = false) String userPhone
                                    , @RequestParam(name = "userEmail", required = false) String userEmail) {

        UserSearchDTO searchDTO = new UserSearchDTO(userName, userPhone, userEmail);

        UserSearchIdResponseDTO responseDTO = memberService.searchId(searchDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param userId
     * @param userName
     * @param userEmail
     *
     * 비밀번호 찾기
     * 사용자가 전달한 정보를 통해 사용자 검증을 한 뒤
     * 데이터베이스에 저장된 이메일로 인증번호를 보낸다.
     * 정상적으로 처리되면 메세지를 담은 응답 전달.
     */
    @GetMapping("/search-pw")
    public ResponseEntity<ResponseMessageDTO> searchPw(@RequestParam(name = "id") String userId
                                                        , @RequestParam(name = "name") String userName
                                                        , @RequestParam(name = "email") String userEmail) {

        UserSearchPwDTO searchDTO = new UserSearchPwDTO(userId, userName, userEmail);

        String responseMessage = memberService.searchPw(searchDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param certificationDTO
     *
     * 인증번호 확인.
     * 사용자가 메일을 확인하고 해당 인증번호를 입력해 확인 요청.
     */
    @PostMapping("/certification")
    public ResponseEntity<ResponseMessageDTO> checkCertification(@RequestBody UserCertificationDTO certificationDTO) {

        String responseMessage = memberService.checkCertificationNo(certificationDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param resetDTO
     *
     * 비밀번호 수정 요청
     */
    @PatchMapping("/reset-pw")
    public ResponseEntity<ResponseMessageDTO> resetPassword(@RequestBody UserResetPwDTO resetDTO) {

        String responseMessage = memberService.resetPw(resetDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }
}
