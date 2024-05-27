package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.member.LoginDTO;
import com.example.mansshop_boot.service.MemberService;
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
    public ResponseEntity<?> loginProc(@RequestBody LoginDTO loginDTO){

        return memberService.loginProc(loginDTO);
    }

    @PostMapping("/post")
    public ResponseEntity<?> postTest() {

        return new ResponseEntity<>(null, HttpStatus.OK);
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

}
