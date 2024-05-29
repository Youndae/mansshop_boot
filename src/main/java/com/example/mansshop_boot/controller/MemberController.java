package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.member.JoinDTO;
import com.example.mansshop_boot.domain.dto.member.LoginDTO;
import com.example.mansshop_boot.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    @Value("#{filePath['file.product.path']}")
    private String filePath;

    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<Long> loginProc(@RequestBody LoginDTO loginDTO
                                        , HttpServletRequest request
                                        , HttpServletResponse response){

        log.info("loginDTO :: id : {}", loginDTO.userId());
        log.info("loginDTO :: pw : {}", loginDTO.userPw());

        return new ResponseEntity<>(memberService.loginProc(loginDTO, request, response), HttpStatus.OK);
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinProc(@RequestBody JoinDTO joinDTO) {

        return new ResponseEntity<>(memberService.joinProc(joinDTO), HttpStatus.OK);
    }

    @GetMapping("/display/{imageName}")
    public ResponseEntity<byte[]> display(@PathVariable(name = "imageName") String imageName) {
        log.info("imageName : {}", imageName);
        File file = new File(filePath + imageName);
        ResponseEntity<byte[]> result = null;

        try{
            HttpHeaders header = new HttpHeaders();
            header.add("Content-Type", Files.probeContentType(file.toPath()));

            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), HttpStatus.OK);
        }catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @GetMapping("/oAuth/token")
    public ResponseEntity<Long> oAuthIssueToken(HttpServletRequest request, HttpServletResponse response) {


        return new ResponseEntity<>(memberService.oAuthUserIssueToken(request, response), HttpStatus.OK);
    }

}
