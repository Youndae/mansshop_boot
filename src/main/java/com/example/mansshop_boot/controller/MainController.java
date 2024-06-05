package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.main.MainListDTO;
import com.example.mansshop_boot.domain.dto.pageable.MemberPageDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseListDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.service.MainService;
import jakarta.servlet.http.HttpServletRequest;
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
import java.security.Principal;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
@Slf4j
public class MainController {

    @Value("#{filePath['file.product.path']}")
    private String filePath;

    private final MainService mainService;

    @GetMapping({"/", "/new"})
    public ResponseEntity<ResponseListDTO> mainList(HttpServletRequest request
                                    , Principal principal) {

        String requestURI = request.getRequestURI();
        String classification = requestURI.substring(requestURI.lastIndexOf("/") + 1);

        classification = classification.equals("") ? "BEST" : classification;

        MemberPageDTO memberPageDTO = MemberPageDTO.builder()
                                                    .pageNum(null)
                                                    .keyword(null)
                                                    .classification(classification)
                                                    .build();

        return new ResponseEntity<>(mainService.getBestAndNewList(memberPageDTO, principal), HttpStatus.OK);
    }

    @GetMapping("/{classification}")
    public ResponseEntity<PagingResponseDTO<MainListDTO>> mainClassificationList(@PathVariable(name = "classification") String classification
                                                            , @RequestParam(name = "page", required = false) Long page
                                                            , Principal principal){

        MemberPageDTO memberPageDTO = MemberPageDTO.builder()
                                                .pageNum(page)
                                                .keyword(null)
                                                .classification(classification)
                                                .build();

        return new ResponseEntity<>(mainService.getClassificationAndSearchList(memberPageDTO, principal), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<PagingResponseDTO<MainListDTO>> searchList(@RequestParam(name = "page", required = false) Long page
                                                                    , @RequestParam(name = "keyword", required = false) String keyword
                                                                    , Principal principal){

        log.info("MainController.Search :: keyword : {}", keyword);
        log.info("MainController.Search :: page : {}", page);

        MemberPageDTO memberPageDTO = MemberPageDTO.builder()
                                                .pageNum(page)
                                                .keyword(keyword)
                                                .classification(null)
                                                .build();

        return new ResponseEntity<>(mainService.getClassificationAndSearchList(memberPageDTO, principal), HttpStatus.OK);
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
