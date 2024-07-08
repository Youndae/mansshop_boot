package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomTokenStealingException;
import com.example.mansshop_boot.domain.dto.main.MainListDTO;
import com.example.mansshop_boot.domain.dto.main.MainListResponseDTO;
import com.example.mansshop_boot.domain.dto.mypage.MemberOrderDTO;
import com.example.mansshop_boot.domain.dto.mypage.MyPageOrderDTO;
import com.example.mansshop_boot.domain.dto.pageable.MemberPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseListDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.service.MainService;
import com.example.mansshop_boot.service.MyPageService;
import com.example.mansshop_boot.service.ResponseMappingService;
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
import java.util.List;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
@Slf4j
public class MainController {

    @Value("#{filePath['file.product.path']}")
    private String filePath;

    private final MainService mainService;

    private final MyPageService myPageService;

    private final ResponseMappingService responseMappingService;

    /**
     *
     * @param request
     * @param principal
     *
     * 메인의 상품 리스트 중 BEST( / ), NEW 리스트 조회.
     * BEST와 NEW는 페이징이 없고 12개의 상품만 출력하기 때문에 다른 카테고리 리스트 조회와 분리.
     */
    @GetMapping({"/", "/new"})
    public ResponseEntity<ResponseListDTO<?>> mainList(HttpServletRequest request
                                    , Principal principal) {

        String requestURI = request.getRequestURI();
        String classification = requestURI.substring(requestURI.lastIndexOf("/") + 1);
        classification = classification.equals("") ? "BEST" : classification;
        MemberPageDTO memberPageDTO = MemberPageDTO.builder()
                                                    .pageNum(1)
                                                    .keyword(null)
                                                    .classification(classification)
                                                    .build();

        List<MainListResponseDTO> responseDTO = mainService.getBestAndNewList(memberPageDTO, principal);

        return responseMappingService.mappingResponseListDTO(responseDTO, principal);
    }

    /**
     *
     * @param classification
     * @param page
     * @param principal
     *
     * 메인의 상품 리스트 중 상품 카테고리 선택으로 인한 리스트 조회.
     */
    @GetMapping("/{classification}")
    public ResponseEntity<PagingResponseDTO<?>> mainClassificationList(@PathVariable(name = "classification") String classification
                                                            , @RequestParam(name = "page") int page
                                                            , Principal principal){

        MemberPageDTO memberPageDTO = MemberPageDTO.builder()
                                                .pageNum(page)
                                                .keyword(null)
                                                .classification(classification)
                                                .build();

        PagingListDTO<MainListResponseDTO> responseDTO = mainService.getClassificationAndSearchList(memberPageDTO, principal);

        return responseMappingService.mappingPagingResponseDTO(responseDTO, principal);
    }

    /**
     *
     * @param page
     * @param keyword
     * @param principal
     *
     * Navbar의 상품 검색 기능
     */
    @GetMapping("/search")
    public ResponseEntity<PagingResponseDTO<?>> searchList(@RequestParam(name = "page") int page
                                                                    , @RequestParam(name = "keyword") String keyword
                                                                    , Principal principal){

        MemberPageDTO memberPageDTO = MemberPageDTO.builder()
                                                .pageNum(page)
                                                .keyword(keyword)
                                                .classification(null)
                                                .build();
        PagingListDTO<MainListResponseDTO> responseDTO = mainService.getClassificationAndSearchList(memberPageDTO, principal);

        return responseMappingService.mappingPagingResponseDTO(responseDTO, principal);
    }

    /**
     *
     * @param imageName
     *
     * 이미지 파일 출력을 위해 blob으로 반환
     */
    @GetMapping("/display/{imageName}")
    public ResponseEntity<byte[]> display(@PathVariable(name = "imageName") String imageName) {
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

    /**
     *
     * @param recipient
     * @param phone
     * @param term
     * @param page
     *
     * 비회원의 주문 내역 조회
     * 가능성은 별로 없다고 생각하지만 비회원도 같은 recipient와 연락처로 장기간 동안 주문한 경우
     * 해당 기간 동안의 내역을 출력할 수 있어야 한다고 생각해 사용자 주문내역과 마찬가지로 term을 받아 기간별 조회 처리.
     */
    @GetMapping("/order/{term}/{page}")
    public ResponseEntity<PagingResponseDTO<?>> nonMemberOrderList(@RequestParam(name = "recipient") String recipient
                                                , @RequestParam(name = "phone") String phone
                                                , @PathVariable(name = "term") String term
                                                , @PathVariable(name = "page") int page
                                                , Principal principal){

        MemberOrderDTO memberOrderDTO = MemberOrderDTO.builder()
                                            .userId(null)
                                            .recipient(recipient)
                                            .phone(phone)
                                            .build();
        OrderPageDTO orderPageDTO = OrderPageDTO.builder()
                                        .pageNum(page)
                                        .term(term)
                                        .build();


        PagingListDTO<MyPageOrderDTO> responseDTO = myPageService.getOrderList(orderPageDTO, memberOrderDTO);

        return responseMappingService.mappingPagingResponseDTO(responseDTO, principal);
    }
}
