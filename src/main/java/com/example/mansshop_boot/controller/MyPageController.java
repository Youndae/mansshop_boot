package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.mypage.*;
import com.example.mansshop_boot.domain.dto.mypage.qna.*;
import com.example.mansshop_boot.domain.dto.mypage.qna.req.MemberQnAInsertDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.req.MemberQnAModifyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.req.QnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.req.QnAReplyInsertDTO;
import com.example.mansshop_boot.domain.dto.pageable.LikePageDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseIdDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<PagingResponseDTO<MyPageOrderDTO>> getOrderList(@PathVariable(name = "term") String term
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

        PagingResponseDTO<MyPageOrderDTO> responseDTO = myPageService.getOrderList(orderPageDTO, memberOrderDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @GetMapping("/like/{page}")
    public ResponseEntity<PagingResponseDTO<ProductLikeDTO>> getLikeProduct(@PathVariable(name = "page") int page, Principal principal) {
        LikePageDTO pageDTO = new LikePageDTO(page);

        PagingResponseDTO<ProductLikeDTO> responseDTO = myPageService.getLikeList(pageDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @GetMapping("/qna/product/{page}")
    public ResponseEntity<PagingResponseDTO<ProductQnAListDTO>> getProductQnA(@PathVariable(name = "page") int page, Principal principal) {
        MyPagePageDTO pageDTO = new MyPagePageDTO(page);

        PagingResponseDTO<ProductQnAListDTO> responseDTO = myPageService.getProductQnAList(pageDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @GetMapping("/qna/product/detail/{qnaId}")
    public ResponseEntity<ProductQnADetailDTO> getProductQnADetail(@PathVariable(name = "qnaId") long qnaId, Principal principal) {

        ProductQnADetailDTO responseDTO = myPageService.getProductQnADetail(qnaId, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @PostMapping("/qna/product/reply")
    public ResponseEntity<ResponseMessageDTO> postProductQnAReply(@RequestBody QnAReplyInsertDTO insertDTO, Principal principal) {

        String responseMessage = myPageService.postProductQnAReply(insertDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    @PatchMapping("/qna/product/reply")
    public ResponseEntity<ResponseMessageDTO> patchProductQnAReply(@RequestBody QnAReplyDTO replyDTO, Principal principal) {

        String responseMessage = myPageService.patchProductQnAReply(replyDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    @DeleteMapping("/qna/product/{qnaId}")
    public ResponseEntity<ResponseMessageDTO> deleteProductQnA(@PathVariable(name = "qnaId") long qnaId, Principal principal) {

        String responseMessage = myPageService.deleteProductQnA(qnaId, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    @GetMapping("/qna/member/{page}")
    public ResponseEntity<PagingResponseDTO<MemberQnAListDTO>> getMemberQnA(@PathVariable(name = "page") int page, Principal principal) {
        MyPagePageDTO pageDTO = new MyPagePageDTO(page);

        PagingResponseDTO<MemberQnAListDTO> responseDTO = myPageService.getMemberQnAList(pageDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @PostMapping("/qna/member")
    public ResponseEntity<ResponseIdDTO> memberQnAInsert(@RequestBody MemberQnAInsertDTO insertDTO, Principal principal) {

        ResponseIdDTO responseDTO = myPageService.postMemberQnA(insertDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @GetMapping("/qna/member/detail/{qnaId}")
    public ResponseEntity<MemberQnADetailDTO> getMemberQnADetail(@PathVariable(name = "qnaId") long qnaId, Principal principal) {

        MemberQnADetailDTO responseDTO = myPageService.getMemberQnADetail(qnaId, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @PostMapping("/qna/member/reply")
    public ResponseEntity<ResponseMessageDTO> patchMemberQnAReply(@RequestBody QnAReplyInsertDTO insertDTO, Principal principal) {

        String responseMessage = myPageService.postMemberQnAReply(insertDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    @PatchMapping("/qna/member/reply")
    public ResponseEntity<ResponseMessageDTO> patchMemberQnAReply(@RequestBody QnAReplyDTO replyDTO, Principal principal) {

        String responseMessage = myPageService.patchMemberQnAReply(replyDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    @GetMapping("/qna/member/modify/{qnaId}")
    public ResponseEntity<ResponseDTO<MemberQnAModifyDataDTO>> getModifyData(@PathVariable(name = "qnaId") long qnaId, Principal principal) {

        ResponseDTO<MemberQnAModifyDataDTO> responseDTO = myPageService.getModifyData(qnaId, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @PatchMapping("/qna/member")
    public ResponseEntity<ResponseMessageDTO> patchModifyData(@RequestBody MemberQnAModifyDTO modifyDTO, Principal principal) {

        String responseMessage = myPageService.patchMemberQnA(modifyDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    @DeleteMapping("/qna/member/{qnaId}")
    public ResponseEntity<ResponseMessageDTO> deleteMemberQnA(@PathVariable(name = "qnaId") long qnaId, Principal principal) {

        String responseMessage = myPageService.deleteMemberQnA(qnaId, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    @GetMapping("/classification")
    public ResponseEntity<QnAClassificationResponseDTO> getQnAClassification(Principal principal) {

        QnAClassificationResponseDTO responseDTO = myPageService.getQnAClassification(principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }


}
