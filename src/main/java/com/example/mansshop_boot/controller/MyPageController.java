package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.mypage.business.MemberOrderDTO;
import com.example.mansshop_boot.domain.dto.mypage.business.MyPagePageDTO;
import com.example.mansshop_boot.domain.dto.mypage.in.MyPageInfoPatchDTO;
import com.example.mansshop_boot.domain.dto.mypage.in.MyPagePatchReviewDTO;
import com.example.mansshop_boot.domain.dto.mypage.in.MyPagePostReviewDTO;
import com.example.mansshop_boot.domain.dto.mypage.out.*;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.MemberQnAInsertDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.MemberQnAModifyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyInsertDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.out.*;
import com.example.mansshop_boot.domain.dto.pageable.LikePageDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.*;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.ResponseWrappingDTO;
import com.example.mansshop_boot.service.MyPageService;
import com.example.mansshop_boot.service.ResponseMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/my-page")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
public class MyPageController {

    private final MyPageService myPageService;

    private final ResponseMappingService responseMappingService;

    /**
     *
     * @param term
     * @param page
     * @param principal
     *
     * 사용자의 주문 내역 조회
     * term으로는 3, 6, 12, all을 받는다.
     * 각 개월수를 의미.
     */
    @GetMapping("/order/{term}/{page}")
    public ResponseEntity<PagingResponseDTO<?>> getOrderList(@PathVariable(name = "term") String term
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

        PagingListDTO<MyPageOrderDTO> responseDTO = myPageService.getOrderList(orderPageDTO, memberOrderDTO);
        return responseMappingService.mappingPagingResponseDTO(responseDTO, principal);
    }

    /**
     *
     * @param page
     * @param principal
     *
     * 관심상품으로 등록된 상품의 리스트 조회
     */
    @GetMapping("/like/{page}")
    public ResponseEntity<PagingResponseDTO<?>> getLikeProduct(@PathVariable(name = "page") int page, Principal principal) {
        LikePageDTO pageDTO = new LikePageDTO(page);
        Page<ProductLikeDTO> responseDTO = myPageService.getLikeList(pageDTO, principal);

        return responseMappingService.mappingPageableResponseDTO(responseDTO, principal);
    }

    /**
     *
     * @param page
     * @param principal
     *
     * 사용자의 상품 문의 리스트 조회.
     */
    @GetMapping("/qna/product/{page}")
    public ResponseEntity<PagingResponseDTO<?>> getProductQnA(@PathVariable(name = "page") int page, Principal principal) {
        MyPagePageDTO pageDTO = new MyPagePageDTO(page);
        Page<ProductQnAListDTO> responseDTO = myPageService.getProductQnAList(pageDTO, principal);

        return responseMappingService.mappingPageableResponseDTO(responseDTO, principal);
    }

    /**
     *
     * @param qnaId
     * @param principal
     *
     * 사용자의 상품 문의 상세 페이지 데이터 조회
     */
    @GetMapping("/qna/product/detail/{qnaId}")
    public ResponseEntity<ResponseDTO<?>> getProductQnADetail(@PathVariable(name = "qnaId") long qnaId, Principal principal) {
        ResponseWrappingDTO<ProductQnADetailDTO> wrappingDTO = new ResponseWrappingDTO<>(myPageService.getProductQnADetail(qnaId, principal));

        return responseMappingService.mappingResponseDTO(wrappingDTO, principal);
    }

    /**
     *
     * @param qnaId
     * @param principal
     *
     * 사용자의 상품 문의 삭제 요청
     */
    @DeleteMapping("/qna/product/{qnaId}")
    public ResponseEntity<ResponseMessageDTO> deleteProductQnA(@PathVariable(name = "qnaId") long qnaId, Principal principal) {

        String responseMessage = myPageService.deleteProductQnA(qnaId, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param page
     * @param principal
     *
     * 사용자의 회원 문의 내역 리스트 조회
     */
    @GetMapping("/qna/member/{page}")
    public ResponseEntity<PagingResponseDTO<?>> getMemberQnA(@PathVariable(name = "page") int page, Principal principal) {
        MyPagePageDTO pageDTO = new MyPagePageDTO(page);

        Page<MemberQnAListDTO> responseDTO = myPageService.getMemberQnAList(pageDTO, principal);

        return responseMappingService.mappingPageableResponseDTO(responseDTO, principal);
    }

    /**
     *
     * @param insertDTO
     * @param principal
     *
     * 사용자의 회원 문의 내역 작성
     */
    @PostMapping("/qna/member")
    public ResponseEntity<ResponseIdDTO<Long>> memberQnAInsert(@RequestBody MemberQnAInsertDTO insertDTO, Principal principal) {

        Long responseId = myPageService.postMemberQnA(insertDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseIdDTO<>(responseId));
    }

    /**
     *
     * @param qnaId
     * @param principal
     *
     * 사용자의 회원 문의 상세 데이터 조회
     */
    @GetMapping("/qna/member/detail/{qnaId}")
    public ResponseEntity<ResponseDTO<?>> getMemberQnADetail(@PathVariable(name = "qnaId") long qnaId, Principal principal) {
        ResponseWrappingDTO<MemberQnADetailDTO> wrappingDTO = new ResponseWrappingDTO<>(myPageService.getMemberQnADetail(qnaId, principal));

        return responseMappingService.mappingResponseDTO(wrappingDTO, principal);
    }

    /**
     *
     * @param insertDTO
     * @param principal
     *
     * 사용자의 회원 문의 답변 작성
     */
    @PostMapping("/qna/member/reply")
    public ResponseEntity<ResponseMessageDTO> postMemberQnAReply(@RequestBody QnAReplyInsertDTO insertDTO, Principal principal) {

        String responseMessage = myPageService.postMemberQnAReply(insertDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param replyDTO
     * @param principal
     *
     * 사용자의 회원 문의 답변 수정
     */
    @PatchMapping("/qna/member/reply")
    public ResponseEntity<ResponseMessageDTO> patchMemberQnAReply(@RequestBody QnAReplyDTO replyDTO, Principal principal) {

        String responseMessage = myPageService.patchMemberQnAReply(replyDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param qnaId
     * @param principal
     *
     * 사용자의 회원 문의 수정시 필요한 데이터 요청
     */
    @GetMapping("/qna/member/modify/{qnaId}")
    public ResponseEntity<ResponseDTO<?>> getModifyData(@PathVariable(name = "qnaId") long qnaId, Principal principal) {
        ResponseWrappingDTO<MemberQnAModifyDataDTO> wrappingDTO = new ResponseWrappingDTO<>(myPageService.getModifyData(qnaId, principal));

        return responseMappingService.mappingResponseDTO(wrappingDTO, principal);
    }

    /**
     *
     * @param modifyDTO
     * @param principal
     *
     * 사용자의 회원 문의 수정
     */
    @PatchMapping("/qna/member")
    public ResponseEntity<ResponseMessageDTO> patchModifyData(@RequestBody MemberQnAModifyDTO modifyDTO, Principal principal) {

        String responseMessage = myPageService.patchMemberQnA(modifyDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param qnaId
     * @param principal
     *
     * 사용자의 회원 문의 삭제
     */
    @DeleteMapping("/qna/member/{qnaId}")
    public ResponseEntity<ResponseMessageDTO> deleteMemberQnA(@PathVariable(name = "qnaId") long qnaId, Principal principal) {

        String responseMessage = myPageService.deleteMemberQnA(qnaId, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param principal
     *
     * 상품 문의 작성 또는 수정 시 문의 카테고리 설정을 위한 카테고리 리스트 조회.
     */
    @GetMapping("/classification")
    public ResponseEntity<ResponseListDTO<?>> getQnAClassification(Principal principal) {
        List<QnAClassificationDTO> responseDTO = myPageService.getQnAClassification(principal);

        return responseMappingService.mappingResponseListDTO(responseDTO, principal);
    }

    /**
     *
     * @param page
     * @param principal
     *
     * 사용자의 작성한 리뷰 리스트 조회
     */
    @GetMapping("/review/{page}")
    public ResponseEntity<PagingResponseDTO<?>> getReview(@PathVariable(name = "page") int page, Principal principal) {
        MyPagePageDTO pageDTO = new MyPagePageDTO(page);
        Page<MyPageReviewDTO> responseDTO = myPageService.getReview(pageDTO, principal);

        return responseMappingService.mappingPageableResponseDTO(responseDTO, principal);
    }

    /**
     *
     * @param reviewId
     * @param principal
     *
     * 사용자의 작성한 리뷰 수정 데이터 요청
     */
    @GetMapping("/review/modify/{reviewId}")
    public ResponseEntity<ResponseDTO<?>> getPatchReviewData(@PathVariable(name = "reviewId") long reviewId, Principal principal) {
        ResponseWrappingDTO<MyPagePatchReviewDataDTO> wrappingDTO = new ResponseWrappingDTO<>(myPageService.getPatchReview(reviewId, principal));

        return responseMappingService.mappingResponseDTO(wrappingDTO, principal);
    }

    /**
     *
     * @param reviewDTO
     * @param principal
     *
     * 사용자의 리뷰 작성
     */
    @PostMapping("/review")
    public ResponseEntity<ResponseMessageDTO> postReview(@RequestBody MyPagePostReviewDTO reviewDTO, Principal principal) {

        String responseMessage = myPageService.postReview(reviewDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param reviewDTO
     * @param principal
     *
     * 사용자의 리뷰 수정
     */
    @PatchMapping("/review")
    public ResponseEntity<ResponseMessageDTO> patchReview(@RequestBody MyPagePatchReviewDTO reviewDTO, Principal principal) {


        String responseMessage = myPageService.patchReview(reviewDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param reviewId
     * @param principal
     *
     * 사용자의 리뷰 삭제
     */
    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<ResponseMessageDTO> deleteReview(@PathVariable(name = "reviewId") long reviewId, Principal principal) {

        String responseMessage = myPageService.deleteReview(reviewId, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param principal
     *
     * 사용자의 정보 수정 데이터 요청
     */
    @GetMapping("/info")
    public ResponseEntity<ResponseDTO<?>> getInfo(Principal principal) {
        ResponseWrappingDTO<MyPageInfoDTO> wrappingDTO = new ResponseWrappingDTO<>(myPageService.getInfo(principal));

        return responseMappingService.mappingResponseDTO(wrappingDTO, principal);
    }

    /**
     *
     * @param infoDTO
     * @param principal
     *
     * 사용자의 정보 수정 요청
     */
    @PatchMapping("/info")
    public ResponseEntity<ResponseMessageDTO> patchInfo(@RequestBody MyPageInfoPatchDTO infoDTO, Principal principal) {

        String responseMessage = myPageService.patchInfo(infoDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

}
