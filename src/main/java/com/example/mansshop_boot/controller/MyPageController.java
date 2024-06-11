package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.mypage.MemberOrderDTO;
import com.example.mansshop_boot.domain.dto.mypage.MyPagePageDTO;
import com.example.mansshop_boot.domain.dto.pageable.LikePageDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/my-page")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;


    @GetMapping("/order/{term}/{page}")
    public ResponseEntity<?> getOrderList(@PathVariable(name = "term") String term
                                        , @PathVariable(name = "page") int page
                                        , Principal principal) {

        OrderPageDTO orderPageDTO = OrderPageDTO.builder()
                .term(term)
                .pageNum(page)
                .build();

        MemberOrderDTO memberOrderDTO = MemberOrderDTO.builder()
                                        .userId(principal.getName())
                                        .recipient(null)
                                        .phone(null)
                                        .build();


        return myPageService.getOrderList(orderPageDTO, memberOrderDTO);
    }

    @GetMapping("/like/{page}")
    public ResponseEntity<?> getLikeProduct(@PathVariable(name = "page") int page, Principal principal) {
        LikePageDTO pageDTO = new LikePageDTO(page);

        return myPageService.getLikeList(pageDTO, principal);
    }

    @GetMapping("/qna/product/{page}")
    public ResponseEntity<?> getProductQnA(@PathVariable(name = "page") int page, Principal principal) {
        MyPagePageDTO pageDTO = new MyPagePageDTO(page);

        return myPageService.getProductQnAList(pageDTO, principal);
    }

    @GetMapping("/qna/product/detail/{qnaId}")
    public ResponseEntity<?> getProductQnADetail(@PathVariable(name = "qnaId") long qnaId, Principal principal) {

        return myPageService.getProductQnADetail(qnaId, principal);
    }

    @GetMapping("/qna/member/{page}")
    public ResponseEntity<?> getMemberQnA(@PathVariable(name = "page") int page, Principal principal) {
        MyPagePageDTO pageDTO = new MyPagePageDTO(page);

        return myPageService.getMemberQnAList(pageDTO, principal);
    }

    @GetMapping("/qna/member/detail/{qnaId}")
    public ResponseEntity<?> getMemberQnADetail(@PathVariable(name = "qnaId") long qnaId, Principal principal) {

        return myPageService.getMemberQnADetail(qnaId, principal);
    }
}
