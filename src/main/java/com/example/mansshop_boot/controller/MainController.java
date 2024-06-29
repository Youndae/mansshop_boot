package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.main.MainListDTO;
import com.example.mansshop_boot.domain.dto.main.MainListResponseDTO;
import com.example.mansshop_boot.domain.dto.mypage.MemberOrderDTO;
import com.example.mansshop_boot.domain.dto.mypage.MyPageOrderDTO;
import com.example.mansshop_boot.domain.dto.pageable.MemberPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseListDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.service.MainService;
import com.example.mansshop_boot.service.MyPageService;
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

    private final MyPageService myPageService;

    @GetMapping({"/", "/new"})
    public ResponseEntity<ResponseListDTO<MainListResponseDTO>> mainList(HttpServletRequest request
                                    , Principal principal) {

        String requestURI = request.getRequestURI();
        String classification = requestURI.substring(requestURI.lastIndexOf("/") + 1);

        classification = classification.equals("") ? "BEST" : classification;

        MemberPageDTO memberPageDTO = MemberPageDTO.builder()
                                                    .pageNum(1)
                                                    .keyword(null)
                                                    .classification(classification)
                                                    .build();

        ResponseListDTO<MainListResponseDTO> responseDTO = mainService.getBestAndNewList(memberPageDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @GetMapping("/{classification}")
    public ResponseEntity<PagingResponseDTO<MainListResponseDTO>> mainClassificationList(@PathVariable(name = "classification") String classification
                                                            , @RequestParam(name = "page") int page
                                                            , Principal principal){

        MemberPageDTO memberPageDTO = MemberPageDTO.builder()
                                                .pageNum(page)
                                                .keyword(null)
                                                .classification(classification)
                                                .build();

        PagingResponseDTO<MainListResponseDTO> responseDTO = mainService.getClassificationAndSearchList(memberPageDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @GetMapping("/search")
    public ResponseEntity<PagingResponseDTO<MainListResponseDTO>> searchList(@RequestParam(name = "page") int page
                                                                    , @RequestParam(name = "keyword") String keyword
                                                                    , Principal principal){

        MemberPageDTO memberPageDTO = MemberPageDTO.builder()
                                                .pageNum(page)
                                                .keyword(keyword)
                                                .classification(null)
                                                .build();

        PagingResponseDTO<MainListResponseDTO> responseDTO = mainService.getClassificationAndSearchList(memberPageDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
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

    @GetMapping("/order/{term}/{page}")
    public ResponseEntity<PagingResponseDTO<MyPageOrderDTO>> nonMemberOrderList(@RequestParam(name = "recipient") String recipient
                                                , @RequestParam(name = "phone") String phone
                                                , @PathVariable(name = "term") String term
                                                , @PathVariable(name = "page") int page){

        MemberOrderDTO memberOrderDTO = MemberOrderDTO.builder()
                                            .userId(null)
                                            .recipient(recipient)
                                            .phone(phone)
                                            .build();
        OrderPageDTO orderPageDTO = OrderPageDTO.builder()
                                        .pageNum(page)
                                        .term(term)
                                        .build();


        PagingResponseDTO<MyPageOrderDTO> responseDTO = myPageService.getOrderList(orderPageDTO, memberOrderDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }
}
