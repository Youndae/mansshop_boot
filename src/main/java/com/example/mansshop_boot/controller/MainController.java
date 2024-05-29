package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.main.MainListDTO;
import com.example.mansshop_boot.domain.dto.pageable.MemberPageDTO;
import com.example.mansshop_boot.domain.dto.response.MainResponseDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.service.MainService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final MainService mainService;

    @GetMapping({"/", "/new"})
    public ResponseEntity<MainResponseDTO> mainList(HttpServletRequest request
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
}
