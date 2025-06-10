package com.example.mansshop_boot.controller.admin;

import com.example.mansshop_boot.annotation.swagger.DefaultApiResponse;
import com.example.mansshop_boot.annotation.swagger.SwaggerAuthentication;
import com.example.mansshop_boot.domain.dto.admin.out.AdminQnAClassificationDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminQnAListResponseDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyInsertDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.out.MemberQnADetailDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.out.ProductQnADetailDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.service.MyPageService;
import com.example.mansshop_boot.service.ResponseMappingService;
import com.example.mansshop_boot.service.admin.AdminQnAService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
public class AdminQnAController {

    private final AdminQnAService adminQnAService;

    private final MyPageService myPageService;

    private final ResponseMappingService responseMappingService;
    /**
     *
     * @param keyword
     * @param page
     * @param listType
     *
     * 상품 문의 리스트
     */
    @Operation(summary = "상품 문의 리스트")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "page",
                    description = "페이지 번호",
                    example = "1",
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "type",
                    description = "조회 리스트 타입. new 또는 all",
                    example = "all",
                    required = true,
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "keyword",
                    description = "검색어(닉네임 또는 아이디)",
                    example = "tester1",
                    in = ParameterIn.QUERY
            )
    })
    @GetMapping("/qna/product")
    public ResponseEntity<PagingResponseDTO<AdminQnAListResponseDTO>> getProductQnA(@RequestParam(name = "keyword", required = false) String keyword,
                                                                                    @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                                                    @RequestParam(name = "type") String listType) {

        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(keyword, listType, page);
        PagingListDTO<AdminQnAListResponseDTO> responseDTO = adminQnAService.getProductQnAList(pageDTO);

        return responseMappingService.mappingPagingResponseDTO(responseDTO);
    }

    /**
     *
     * @param qnaId
     *
     * 상품 문의 상세 정보 조회
     */
    @Operation(summary = "상품 문의 상세 정보 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "qnaId",
            description = "상품 문의 아이디",
            example = "1",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/qna/product/{qnaId}")
    public ResponseEntity<ProductQnADetailDTO> getProductQnADetail(@PathVariable(name = "qnaId") long qnaId) {


        ProductQnADetailDTO responseDTO = myPageService.getProductQnADetailData(qnaId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param qnaId
     *
     * 상품 문의 답변 완료 상태로 수정
     */
    @Operation(summary = "상품 문의 답변 상태를 완료로 수정")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "qnaId",
            description = "상품 문의 아이디",
            example = "1",
            required = true,
            in = ParameterIn.PATH
    )
    @PatchMapping("/qna/product/{qnaId}")
    public ResponseEntity<ResponseMessageDTO> patchProductQnAComplete(@PathVariable(name = "qnaId") long qnaId) {

        String responseMessage = adminQnAService.patchProductQnAComplete(qnaId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param insertDTO
     * @param principal
     *
     * 관리자의 상품 문의 답변 작성
     */
    @Operation(summary = "상품 문의 답변 작성")
    @DefaultApiResponse
    @SwaggerAuthentication
    @PostMapping("/qna/product/reply")
    public ResponseEntity<ResponseMessageDTO> postProductQnAReply(@RequestBody QnAReplyInsertDTO insertDTO, Principal principal) {

        String responseMessage = adminQnAService.postProductQnAReply(insertDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param replyDTO
     * @param principal
     *
     * 관리자의 상품 문의 답변 수정
     */
    @Operation(summary = "상품 문의 답변 수정")
    @DefaultApiResponse
    @SwaggerAuthentication
    @PatchMapping("/qna/product/reply")
    public ResponseEntity<ResponseMessageDTO> patchProductQnAReply(@RequestBody QnAReplyDTO replyDTO, Principal principal) {

        String responseMessage = adminQnAService.patchProductQnAReply(replyDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param keyword
     * @param page
     * @param listType
     *
     * 관리자의 회원 문의 리스트 조회
     */
    @Operation(summary = "회원 문의 리스트 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "page",
                    description = "페이지 번호",
                    example = "1",
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "listType",
                    description = "조회 리스트 타입. new 또는 all",
                    example = "all",
                    required = true,
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "keyword",
                    description = "검색어(아이디 또는 닉네임)",
                    example = "tester1",
                    in = ParameterIn.QUERY
            )
    })
    @GetMapping("/qna/member")
    public ResponseEntity<PagingResponseDTO<AdminQnAListResponseDTO>> getMemberQnA(@RequestParam(name = "keyword", required = false) String keyword,
                                                                                   @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                                                   @RequestParam(name = "type") String listType) {

        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(keyword, listType, page);
        PagingListDTO<AdminQnAListResponseDTO> responseDTO = adminQnAService.getMemberQnAList(pageDTO);

        return responseMappingService.mappingPagingResponseDTO(responseDTO);
    }

    /**
     *
     * @param qnaId
     *
     * 관리자의 회원 문의 상세 조회
     */
    @Operation(summary = "회원 문의 상세 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "qnaId",
            description = "회원 문의 아이디",
            example = "1",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/qna/member/{qnaId}")
    public ResponseEntity<MemberQnADetailDTO> getMemberQnADetail(@PathVariable(name = "qnaId") long qnaId) {

        MemberQnADetailDTO responseDTO = myPageService.getMemberQnADetailData(qnaId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param qnaId
     *
     * 관리자의 회원 문의 답변 완료 처리
     */
    @Operation(summary = "회원 문의 답변 상태 완료로 수정")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "qnaId",
            description = "회원 문의 아이디",
            example = "1",
            required = true,
            in = ParameterIn.PATH
    )
    @PatchMapping("/qna/member/{qnaId}")
    public ResponseEntity<ResponseMessageDTO> patchMemberQnAComplete(@PathVariable(name = "qnaId") long qnaId) {
        String responseMessage = adminQnAService.patchMemberQnAComplete(qnaId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param insertDTO
     * @param principal
     *
     * 관리자의 회원 문의 답변 작성.
     */
    @Operation(summary = "회원 문의 답변 작성")
    @DefaultApiResponse
    @SwaggerAuthentication
    @PostMapping("/qna/member/reply")
    public ResponseEntity<ResponseMessageDTO> postMemberQnAReply(@RequestBody QnAReplyInsertDTO insertDTO, Principal principal) {

        String responseMessage = adminQnAService.postMemberQnAReply(insertDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param replyDTO
     * @param principal
     *
     * 관리자의 회원 문의 답변 수정.
     */
    @Operation(summary = "회원 문의 답변 수정")
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
     * 관리자의 회원 문의 분류 조회
     */
    @Operation(summary = "회원 문의 분류 조회",
            description = "회원 문의 분류 작성 기능 시 모든 분류 데이터를 출력하기 위해 사용"
    )
    @DefaultApiResponse
    @SwaggerAuthentication
    @GetMapping("/qna/classification")
    public ResponseEntity<List<AdminQnAClassificationDTO>> getQnAClassification() {

        List<AdminQnAClassificationDTO> responseDTO = adminQnAService.getQnAClassification();

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param classification
     *
     * 관리자의 회원 문의 분류 추가
     */
    @Operation(summary = "회원 문의 분류 추가", description = "분류명만 담아 요청")
    @DefaultApiResponse
    @SwaggerAuthentication
    @PostMapping("/qna/classification")
    public ResponseEntity<ResponseMessageDTO> postQnAClassification(@RequestBody String classification) {
        String responseMessage = adminQnAService.postQnAClassification(classification);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param classificationId
     *
     * 관리자의 회원문의 분류 제거
     */
    @Operation(summary = "회원 문의 분류 삭제")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "qnaClassificationId",
            description = "삭제할 회원 문의 분류 아이디",
            example = "1",
            required = true,
            in = ParameterIn.PATH
    )
    @DeleteMapping("/qna/classification/{qnaClassificationId}")
    public ResponseEntity<ResponseMessageDTO> deleteQnAClassification(@PathVariable(name = "qnaClassificationId") Long classificationId) {

        String responseMessage = adminQnAService.deleteQnAClassification(classificationId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }
}
