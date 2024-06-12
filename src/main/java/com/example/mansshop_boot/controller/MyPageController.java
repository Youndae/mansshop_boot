package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.mypage.*;
import com.example.mansshop_boot.domain.dto.pageable.LikePageDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/my-page")
@RequiredArgsConstructor
@Slf4j
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

    @PatchMapping("/qna/product/reply")
    public ResponseEntity<?> patchProductQnAReply(@RequestBody QnAReplyDTO replyDTO, Principal principal) {

        return myPageService.patchProductQnAReply(replyDTO, principal);
    }

    @PostMapping("/qna/product/reply")
    public ResponseEntity<?> postProductQnAReply(@RequestBody QnAReplyInsertDTO insertDTO, Principal principal) {

        return myPageService.postProductQnAReply(insertDTO, principal);
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

    @PatchMapping("/qna/member/reply")
    public ResponseEntity<?> patchMemberQnAReply(@RequestBody QnAReplyDTO replyDTO, Principal principal) {

        return myPageService.patchMemberQnAReply(replyDTO, principal);
    }

    @PostMapping("/qna/member/reply")
    public ResponseEntity<?> patchMemberQnAReply(@RequestBody QnAReplyInsertDTO insertDTO, Principal principal) {

        return myPageService.postMemberQnAReply(insertDTO, principal);
    }

    @PostMapping("/qna/member")
    public ResponseEntity<?> memberQnAInsert(@RequestBody MemberQnAInsertDTO insertDTO, Principal principal) {

        log.info("MyPageController.memberQnAInsert :: insertDTO : {}", insertDTO);

        return myPageService.postMemberQnA(insertDTO, principal);
    }

    @GetMapping("/qna/member/modify/{qnaId}")
    public ResponseEntity<?> getModifyData(@PathVariable(name = "qnaId") long qnaId, Principal principal) {

        return myPageService.getModifyData(qnaId, principal);
    }

    @PatchMapping("/qna/member")
    public ResponseEntity<?> getModifyData(@RequestBody MemberQnAModifyDTO modifyDTO, Principal principal) {

        return myPageService.patchMemberQnA(modifyDTO, principal);
    }

    @GetMapping("/classification")
    public ResponseEntity<?> getQnAClassification(Principal principal) {

        return myPageService.getQnAClassification(principal);
    }


}
