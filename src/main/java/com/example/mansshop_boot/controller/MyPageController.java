package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.annotation.swagger.DefaultApiResponse;
import com.example.mansshop_boot.annotation.swagger.SwaggerAuthentication;
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
import com.example.mansshop_boot.service.MyPageService;
import com.example.mansshop_boot.service.ResponseMappingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
    @Operation(summary = "회원의 주문내역 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "term",
                    description = "주문내역 조회 기간. 3, 6, 12, all",
                    example = "3",
                    required = true,
                    in = ParameterIn.PATH
            ),
            @Parameter(name = "page",
                    description = "페이지 번호",
                    example = "1",
                    required = true,
                    in = ParameterIn.PATH
            )
    })
    @GetMapping("/order/{term}")
    public ResponseEntity<PagingResponseDTO<MyPageOrderDTO>> getOrderList(@PathVariable(name = "term") String term,
                                        @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                        Principal principal) {

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
        return responseMappingService.mappingPagingResponseDTO(responseDTO);
    }

    /**
     *
     * @param page
     * @param principal
     *
     * 관심상품으로 등록된 상품의 리스트 조회
     */
    @Operation(summary = "회원의 관심상품 리스트 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "page",
            description = "페이지 번호",
            example = "1",
            in = ParameterIn.QUERY
    )
    @GetMapping("/like")
    public ResponseEntity<PagingResponseDTO<ProductLikeDTO>> getLikeProduct(@RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                                            Principal principal) {
        LikePageDTO pageDTO = new LikePageDTO(page);
        Page<ProductLikeDTO> responseDTO = myPageService.getLikeList(pageDTO, principal);

        return responseMappingService.mappingPageableResponseDTO(responseDTO);
    }

    /**
     *
     * @param page
     * @param principal
     *
     * 사용자의 상품 문의 리스트 조회.
     */
    @Operation(summary = "회원의 작성한 상품 문의 리스트 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "page",
            description = "페이지 번호",
            example = "1",
            in = ParameterIn.QUERY
    )
    @GetMapping("/qna/product")
    public ResponseEntity<PagingResponseDTO<ProductQnAListDTO>> getProductQnA(@RequestParam(name = "page", required = false, defaultValue = "1") int page, Principal principal) {
        MyPagePageDTO pageDTO = new MyPagePageDTO(page);
        Page<ProductQnAListDTO> responseDTO = myPageService.getProductQnAList(pageDTO, principal);

        return responseMappingService.mappingPageableResponseDTO(responseDTO);
    }

    /**
     *
     * @param qnaId
     * @param principal
     *
     * 사용자의 상품 문의 상세 페이지 데이터 조회
     */
    @Operation(summary = "회원의 상품 문의 상세 데이터")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "qnaId",
            description = "상품 문의 아이디",
            example = "1",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/qna/product/detail/{qnaId}")
    public ResponseEntity<ProductQnADetailDTO> getProductQnADetail(@PathVariable(name = "qnaId") long qnaId, Principal principal) {
        ProductQnADetailDTO responseDTO = myPageService.getProductQnADetail(qnaId, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param qnaId
     * @param principal
     *
     * 사용자의 상품 문의 삭제 요청
     */
    @Operation(summary = "회원의 상품 문의 삭제")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "qnaId",
            description = "상품 문의 아이디",
            example = "1",
            required = true,
            in = ParameterIn.PATH
    )
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
    @Operation(summary = "회원의 문의내역 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "page",
            description = "페이지 번호",
            example = "1",
            required = true,
            in = ParameterIn.QUERY
    )
    @GetMapping("/qna/member")
    public ResponseEntity<PagingResponseDTO<MemberQnAListDTO>> getMemberQnA(@RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                                            Principal principal) {
        MyPagePageDTO pageDTO = new MyPagePageDTO(page);

        Page<MemberQnAListDTO> responseDTO = myPageService.getMemberQnAList(pageDTO, principal);

        return responseMappingService.mappingPageableResponseDTO(responseDTO);
    }

    /**
     *
     * @param insertDTO
     * @param principal
     *
     * 사용자의 회원 문의 내역 작성
     */
    @Operation(summary = "회원의 문의 작성 요청")
    @DefaultApiResponse
    @SwaggerAuthentication
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
    @Operation(summary = "회원의 문의 상세 데이터 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "qnaId",
            description = "회원 문의 아이디",
            example = "1",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/qna/member/detail/{qnaId}")
    public ResponseEntity<MemberQnADetailDTO> getMemberQnADetail(@PathVariable(name = "qnaId") long qnaId, Principal principal) {
        MemberQnADetailDTO responseDTO = myPageService.getMemberQnADetail(qnaId, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param insertDTO
     * @param principal
     *
     * 사용자의 회원 문의 답변 작성
     */
    @Operation(summary = "회원의 문의 답변 작성")
    @DefaultApiResponse
    @SwaggerAuthentication
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
    @Operation(summary = "회원의 문의 답변 수정")
    @DefaultApiResponse
    @SwaggerAuthentication
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
    @Operation(summary = "회원의 문의 수정 기능 데이터 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "qnaId",
            description = "회원 문의 아이디",
            example = "1",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/qna/member/modify/{qnaId}")
    public ResponseEntity<MemberQnAModifyDataDTO> getModifyData(@PathVariable(name = "qnaId") long qnaId, Principal principal) {
        MemberQnAModifyDataDTO responseDTO = myPageService.getModifyData(qnaId, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }


    /**
     *
     * @param modifyDTO
     * @param principal
     *
     * 사용자의 회원 문의 수정
     */
    @Operation(summary = "회원의 문의 수정 요청")
    @DefaultApiResponse
    @SwaggerAuthentication
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
    @Operation(summary = "회원의 문의 삭제 요청")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "qnaId",
            description = "문의 아이디",
            example = "1",
            required = true,
            in = ParameterIn.PATH
    )
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
    @Operation(summary = "회원의 문의 작성 시 필요한 문의 분류 리스트 조회",
            description = "관리자에도 동일한 기능이 있으나, 거기에서는 모든 분류를 조회하고 여기에서는 노출되어야 할 분류만 조회"
    )
    @DefaultApiResponse
    @SwaggerAuthentication
    @GetMapping("/classification")
    public ResponseEntity<List<QnAClassificationDTO>> getQnAClassification(Principal principal) {
        List<QnAClassificationDTO> responseDTO = myPageService.getQnAClassification(principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param page
     * @param principal
     *
     * 사용자의 작성한 리뷰 리스트 조회
     */
    @Operation(summary = "회원의 작성한 리뷰 목록 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "page",
            description = "페이지 번호",
            example = "1",
            required = true,
            in = ParameterIn.QUERY
    )
    @GetMapping("/review")
    public ResponseEntity<PagingResponseDTO<MyPageReviewDTO>> getReview(@RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                                        Principal principal) {
        MyPagePageDTO pageDTO = new MyPagePageDTO(page);
        Page<MyPageReviewDTO> responseDTO = myPageService.getReview(pageDTO, principal);

        return responseMappingService.mappingPageableResponseDTO(responseDTO);
    }

    /**
     *
     * @param reviewId
     * @param principal
     *
     * 사용자의 작성한 리뷰 수정 데이터 요청
     */
    @Operation(summary = "회원의 리뷰 수정 기능 데이터 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "reviewId",
            description = "리뷰 아이디",
            example = "1",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/review/modify/{reviewId}")
    public ResponseEntity<MyPagePatchReviewDataDTO> getPatchReviewData(@PathVariable(name = "reviewId") long reviewId, Principal principal) {
        MyPagePatchReviewDataDTO responseDTO = myPageService.getPatchReview(reviewId, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param reviewDTO
     * @param principal
     *
     * 사용자의 리뷰 작성
     */
    @Operation(summary = "회원의 리뷰 작성")
    @DefaultApiResponse
    @SwaggerAuthentication
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
    @Operation(summary = "회원의 리뷰 수정")
    @DefaultApiResponse
    @SwaggerAuthentication
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
    @Operation(summary = "회원의 리뷰 삭제")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "reviewId",
            description = "리뷰 아이디",
            example = "1",
            required = true,
            in = ParameterIn.PATH
    )
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
    @Operation(summary = "회원의 정보 수정 기능 데이터 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @GetMapping("/info")
    public ResponseEntity<MyPageInfoDTO> getInfo(Principal principal) {
        MyPageInfoDTO responseDTO = myPageService.getInfo(principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param infoDTO
     * @param principal
     *
     * 사용자의 정보 수정 요청
     */
    @Operation(summary = "회원의 정보 수정 요청")
    @DefaultApiResponse
    @SwaggerAuthentication
    @PatchMapping("/info")
    public ResponseEntity<ResponseMessageDTO> patchInfo(@RequestBody MyPageInfoPatchDTO infoDTO, Principal principal) {

        String responseMessage = myPageService.patchInfo(infoDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

}
