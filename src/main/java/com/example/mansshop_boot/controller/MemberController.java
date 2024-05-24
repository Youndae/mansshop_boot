package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.member.LoginDTO;
import com.example.mansshop_boot.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<?> loginProc(@RequestBody LoginDTO loginDTO){

        return memberService.loginProc(loginDTO);
    }

}
