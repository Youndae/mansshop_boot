package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.annotation.swagger.DefaultApiResponse;
import com.example.mansshop_boot.annotation.swagger.SwaggerAuthentication;
import com.example.mansshop_boot.domain.dto.admin.business.AdminReviewDTO;
import com.example.mansshop_boot.domain.dto.admin.in.*;
import com.example.mansshop_boot.domain.dto.admin.out.*;
import com.example.mansshop_boot.domain.dto.mypage.qna.out.MemberQnADetailDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.out.ProductQnADetailDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyInsertDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.rabbitMQ.FailedQueueDTO;
import com.example.mansshop_boot.domain.dto.response.*;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.ResponseWrappingDTO;
import com.example.mansshop_boot.domain.enumuration.AdminListType;
import com.example.mansshop_boot.service.AdminService;
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
import org.springframework.http.MediaType;
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
public class AdminController {

    private final AdminService adminService;

    private final MyPageService myPageService;

    private final ResponseMappingService responseMappingService;

    /**
     *
     * @param keyword
     * @param page
     * @param principal
     *
     * 관리자 상품 목록 리스트 조회
     */
    @Operation(summary = "관리자 상품 리스트 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "page",
                        description = "페이지 번호.",
                        example = "1",
                        in = ParameterIn.QUERY
            ),
            @Parameter(name = "keyword",
                        description = "검색어. 검색 요청시에만 필요.",
                        example = "DummyOUTER",
                        in = ParameterIn.QUERY
            )
    })
    @GetMapping("/product")
    public ResponseEntity<PagingResponseDTO<AdminProductListDTO>> getProductList(@RequestParam(name = "keyword", required = false) String keyword
                                                                                , @RequestParam(name = "page", required = false, defaultValue = "1") int page
                                                                                , Principal principal){

        PagingListDTO<AdminProductListDTO> responseDTO = adminService.getProductList(new AdminPageDTO(keyword, page));

        return responseMappingService.mappingPagingResponseDTO(responseDTO, principal);
    }

    /**
     *
     * 상품 분류 리스트 조회.
     * 상품 추가 혹은 상품 할인 추가시 조회.
     * 모든 상품 분류명을 반환
     */
    @Operation(summary = "관리자 상품 분류 조회", description = "분류별 리스트 조회가 아닌 상품 분류명 리스트를 반환. 상품 추가 및 할인 추가 시 필요.")
    @DefaultApiResponse
    @SwaggerAuthentication
    @GetMapping("/product/classification")
    public ResponseEntity<ResponseListDTO<String>> getProductClassification(Principal principal) {
        List<String> responseDTO = adminService.getClassification();

        return responseMappingService.mappingResponseListDTO(responseDTO, principal);
    }

    /**
     *
     * @param productId
     *
     * 관리자 상품 상세 정보 조회
     */
    @Operation(summary = "관리자 상품 상세 정보 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "productId",
                description = "상품 아이디",
                example = "BAGS20210629134401",
                required = true,
                in = ParameterIn.PATH
    )
    @GetMapping("/product/detail/{productId}")
    public ResponseEntity<ResponseDTO<AdminProductDetailDTO>> getProductDetail(@PathVariable(name = "productId") String productId
                                                            , Principal principal){

        ResponseWrappingDTO<AdminProductDetailDTO> dto = new ResponseWrappingDTO<>(
                                                                    adminService.getProductDetail(productId)
                                                            );

        return responseMappingService.mappingResponseDTO(dto, principal);
    }

    /**
     *
     * @param postDTO
     * @param imageDTO
     *
     * 관리자의 상품 추가 요청
     * 상품 데이터는 AdminProductPatchDTO
     * 상품 대표 썸네일, 썸네일, 정보 이미지 Multipart는 AdminProductImageDTO
     */
    @Operation(summary = "관리자 상품 추가",
                description = "이 API는 swagger에서 테스트 불가. MultipartFile 또는 List<MultipartFile> 객체들을 DTO 필드로 받고 있기 때문에 Swagger에서 테스트는 불가능."
    )
    @DefaultApiResponse
    @SwaggerAuthentication
    @PostMapping(value = "/product", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResponseIdDTO<String>> postProduct(@ModelAttribute AdminProductPatchDTO postDTO,
                                                             @ModelAttribute AdminProductImageDTO imageDTO) {
        ResponseIdDTO<String> responseDTO = new ResponseIdDTO<>(adminService.postProduct(postDTO, imageDTO));

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param productId
     * @param principal
     *
     * 관리자의 상품 수정 페이지에서 상품 정보 요청
     */
    @Operation(summary = "관리자 상품 수정 페이지 데이터 요청")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "productId",
                description = "상품 아이디",
                example = "BAGS20210629134401",
                required = true,
                in = ParameterIn.PATH
    )
    @GetMapping("/product/patch/{productId}")
    public ResponseEntity<ResponseDTO<AdminProductPatchDataDTO>> getPatchProductData(@PathVariable(name = "productId") String productId
                                                                                    , Principal principal) {
        ResponseWrappingDTO<AdminProductPatchDataDTO> dto = new ResponseWrappingDTO<>(adminService.getPatchProductData(productId));

        return responseMappingService.mappingResponseDTO(dto, principal);
    }

    /**
     *
     * @param productId
     * @param deleteOptionList
     * @param patchDTO
     * @param imageDTO
     *
     * 관리자의 상품 수정
     * 상품 id를 반환
     */
    @Operation(summary = "관리자 상품 수정",
            description = "이 API는 swagger에서 테스트 불가. MultipartFile 또는 List<MultipartFile> 객체들을 DTO 필드로 받고 있기 때문에 Swagger에서 테스트는 불가능."
    )
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "productId",
                        description = "수정할 상품 아이디",
                        example = "BAGS20210629134401",
                        required = true,
                        in = ParameterIn.PATH
            )
    })
    @PatchMapping(value = "/product/{productId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResponseIdDTO<String>> patchProduct(@PathVariable(name = "productId") String productId
                                        , @RequestPart(value = "deleteOptionList", required = false) List<Long> deleteOptionList
                                        , @ModelAttribute AdminProductPatchDTO patchDTO
                                        , @ModelAttribute AdminProductImageDTO imageDTO) {

        ResponseIdDTO<String> responseDTO = new ResponseIdDTO<>(
                                                adminService.patchProduct(productId, deleteOptionList, patchDTO, imageDTO)
                                        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param keyword
     * @param page
     *
     * 관리자 상품 재고 리스트.
     * 재고가 적은순으로 정렬
     */
    @Operation(summary = "상품 재고 리스트 조회",
                description = "재고가 적은순으로 정렬"
    )
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "page",
                        description = "페이지 번호.",
                        example = "1",
                        in = ParameterIn.QUERY
            ),
            @Parameter(name = "keyword",
                        description = "검색어(상품명)",
                        example = "DummyOUTER",
                        in = ParameterIn.QUERY
            )
    })
    @GetMapping("/product/stock")
    public ResponseEntity<PagingResponseDTO<AdminProductStockDTO>> getProductStock(@RequestParam(name = "keyword", required = false) String keyword
                                                                , @RequestParam(name = "page", required = false, defaultValue = "1") int page
                                                                , Principal principal) {
        AdminPageDTO pageDTO = new AdminPageDTO(keyword, page);
        PagingListDTO<AdminProductStockDTO> responseDTO = adminService.getProductStock(pageDTO);

        return responseMappingService.mappingPagingResponseDTO(responseDTO, principal);
    }

    /**
     *
     * @param keyword
     * @param page
     *
     * 할인중인 상품 리스트 조회
     */
    @Operation(summary = "할인중인 상품 리스트 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "page",
                        description = "페이지 번호.",
                        example = "1",
                        in = ParameterIn.QUERY
            ),
            @Parameter(name = "keyword",
                        description = "검색어(상품명)",
                        example = "DummyOUTER",
                        in = ParameterIn.QUERY
            )
    })
    @GetMapping("/product/discount")
    public ResponseEntity<PagingResponseDTO<AdminDiscountResponseDTO>> getDiscountProductList(@RequestParam(name = "keyword", required = false) String keyword
                                                                                            , @RequestParam(name = "page", required = false, defaultValue = "1") int page
                                                                                            , Principal principal) {

        AdminPageDTO pageDTO = new AdminPageDTO(keyword, page);

        PagingListDTO<AdminDiscountResponseDTO> responseDTO = adminService.getDiscountProduct(pageDTO);

        return responseMappingService.mappingPagingResponseDTO(responseDTO, principal);
    }

    /**
     *
     * @param classification
     *
     * 상품 할인 설정에서 상품 select box에 사용될 상품 리스트.
     * 선택한 상품 분류에 따라 그 분류에 해당하는 상품들의 리스트 반환.
     */
    @Operation(summary = "상품 할인 설정에서 선택한 상품 분류에 해당하는 상품 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "classification",
                description = "상품분류 아이디",
                example = "OUTER",
                required = true,
                in = ParameterIn.PATH
    )
    @GetMapping("/product/discount/select/{classification}")
    public ResponseEntity<ResponseListDTO<AdminDiscountProductDTO>> getDiscountProductSelectList(@PathVariable(name = "classification") String classification
                                                                            , Principal principal) {

        List<AdminDiscountProductDTO> responseDTO = adminService.getSelectDiscountProduct(classification);

        return responseMappingService.mappingResponseListDTO(responseDTO, principal);
    }

    /**
     *
     * @param patchDTO
     *
     * 상품 할인 설정.
     */
    @Operation(summary = "상품 할인 설정 요청",
                description = "여러 상품 아이디를 보내 복수의 상품을 동일한 할인율로 설정 가능"
    )
    @DefaultApiResponse
    @SwaggerAuthentication
    @PatchMapping(value = "/product/discount", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseMessageDTO> patchDiscountProduct(@RequestBody AdminDiscountPatchDTO patchDTO) {

        String responseMessage = adminService.patchDiscountProduct(patchDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        new ResponseMessageDTO(responseMessage)
                );
    }

    /**
     *
     * @param searchType
     * @param keyword
     * @param page
     *
     * 모든 주문내역 리스트
     */
    @Operation(summary = "전체 주문내역 목록 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "page",
                        description = "페이지 번호",
                        example = "1",
                        in = ParameterIn.QUERY
            ),
            @Parameter(name = "searchType",
                        description = "검색 타입. recipient, userId",
                        example = "userId",
                        in = ParameterIn.QUERY
            ),
            @Parameter(name = "keyword",
                        description = "검색어",
                        example = "tester1",
                        in = ParameterIn.QUERY
            )
    })
    @GetMapping("/order/all")
    public ResponseEntity<PagingResponseDTO<AdminOrderResponseDTO>> getAllOrder(@RequestParam(name = "searchType", required = false) String searchType
                                        , @RequestParam(value = "keyword", required = false) String keyword
                                        , @RequestParam(name = "page", required = false, defaultValue = "1") int page
                                        , Principal principal) {

        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(keyword, searchType, page);
        PagingListDTO<AdminOrderResponseDTO> responseDTO = adminService.getAllOrderList(pageDTO);

        return responseMappingService.mappingPagingResponseDTO(responseDTO, principal);
    }

    /**
     *
     * @param searchType
     * @param keyword
     * @param page
     *
     * 미처리 주문 내역 리스트
     */
    @Operation(summary = "미처리 주문내역 목록 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "page",
                    description = "페이지 번호",
                    example = "1",
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "searchType",
                    description = "검색 타입. recipient, userId",
                    example = "userId",
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "keyword",
                    description = "검색어",
                    example = "tester1",
                    in = ParameterIn.QUERY
            )
    })
    @GetMapping("/order/new")
    public ResponseEntity<PagingElementsResponseDTO<AdminOrderResponseDTO>> getNewOrder(@RequestParam(name = "searchType", required = false) String searchType
            , @RequestParam(name = "keyword", required = false) String keyword
            , @RequestParam(name = "page", required = false, defaultValue = "1") int page
            , Principal principal) {

        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(keyword, searchType, page);
        PagingListDTO<AdminOrderResponseDTO> responseDTO = adminService.getNewOrderList(pageDTO);

        return responseMappingService.mappingPagingElementsResponseDTO(responseDTO, principal);
    }

    /**
     *
     * @param orderId
     *
     * 주문 처리.
     * 사용자가 결제한 주문 확인중 상태에서 관리자가 주문 확인 버튼을 클릭.
     * 상태를 상품 준비중으로 수정.
     */
    @Operation(summary = "주문 확인 처리")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "orderId",
            description = "주문 아이디",
            example = "1",
            required = true,
            in = ParameterIn.PATH
    )
    @PatchMapping("/order/{orderId}")
    public ResponseEntity<ResponseMessageDTO> patchOrder(@PathVariable(name = "orderId") long orderId) {

        String responseMessage = adminService.orderPreparation(orderId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

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
    public ResponseEntity<PagingResponseDTO<AdminQnAListResponseDTO>> getProductQnA(@RequestParam(name = "keyword", required = false) String keyword
                                        , @RequestParam(name = "page", required = false, defaultValue = "1") int page
                                        , @RequestParam(name = "type") String listType
                                        , Principal principal) {

        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(keyword, listType, page);
        PagingListDTO<AdminQnAListResponseDTO> responseDTO = adminService.getProductQnAList(pageDTO);

        return responseMappingService.mappingPagingResponseDTO(responseDTO, principal);
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
    public ResponseEntity<ResponseDTO<ProductQnADetailDTO>> getProductDetail(@PathVariable(name = "qnaId") long qnaId
                                                                , Principal principal) {


        ResponseWrappingDTO<ProductQnADetailDTO> dto = new ResponseWrappingDTO<>(
                                                                myPageService.getProductQnADetailData(qnaId)
                                                        );

        return responseMappingService.mappingResponseDTO(dto, principal);
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

        String responseMessage = adminService.patchProductQnAComplete(qnaId);

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

        String responseMessage = adminService.postProductQnAReply(insertDTO, principal);

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

        String responseMessage = adminService.patchProductQnAReply(replyDTO, principal);

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
    public ResponseEntity<PagingResponseDTO<AdminQnAListResponseDTO>> getMemberQnA(@RequestParam(name = "keyword", required = false) String keyword
                                        , @RequestParam(name = "page", required = false, defaultValue = "1") int page
                                        , @RequestParam(name = "type") String listType
                                        , Principal principal) {

        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(keyword, listType, page);
        PagingListDTO<AdminQnAListResponseDTO> responseDTO = adminService.getMemberQnAList(pageDTO);

        return responseMappingService.mappingPagingResponseDTO(responseDTO, principal);
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
    public ResponseEntity<ResponseDTO<MemberQnADetailDTO>> getMemberDetail(@PathVariable(name = "qnaId") long qnaId
                                                            , Principal principal) {


        ResponseWrappingDTO<MemberQnADetailDTO> dto = new ResponseWrappingDTO<>(
                                                                myPageService.getMemberQnADetailData(qnaId)
                                                        );

        return responseMappingService.mappingResponseDTO(dto, principal);
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
        String responseMessage = adminService.patchMemberQnAComplete(qnaId);

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

        String responseMessage = adminService.postMemberQnAReply(insertDTO, principal);

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
    public ResponseEntity<ResponseListDTO<AdminQnAClassificationDTO>> getQnAClassification(Principal principal) {

        List<AdminQnAClassificationDTO> responseDTO = adminService.getQnAClassification();

        return responseMappingService.mappingResponseListDTO(responseDTO, principal);
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
        String responseMessage = adminService.postQnAClassification(classification);

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

        String responseMessage = adminService.deleteQnAClassification(classificationId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param keyword
     * @param page
     * @param searchType
     * @param principal
     *
     * 관리자의 새로운 리뷰 리스트 조회.
     */
    @Operation(summary = "새로운 리뷰 리스트 조회",
            description = "검색은 사용자 아이디 및 닉네임 또는 상품명 기반으로 선택 후 검색. 새로운 리뷰의 기준은 답변 처리가 안된 리뷰."
    )
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "page",
                    description = "페이지 번호",
                    example = "1",
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "searchType",
                    description = "검색 타입. user 또는 product",
                    example = "user",
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "keyword",
                    description = "검색어",
                    example = "tester1",
                    in = ParameterIn.QUERY
            )
    })
    @GetMapping("/review")
    public ResponseEntity<PagingResponseDTO<AdminReviewDTO>> getNewReviewList(@RequestParam(name = "keyword", required = false) String keyword
                                        , @RequestParam(name = "page", required = false, defaultValue = "1") int page
                                        , @RequestParam(name = "searchType", required = false) String searchType
                                        , Principal principal) {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(keyword, searchType, page);
        PagingListDTO<AdminReviewDTO> responseDTO = adminService.getReviewList(pageDTO, AdminListType.NEW);

        return responseMappingService.mappingPagingResponseDTO(responseDTO, principal);
    }

    /**
     *
     * @param keyword
     * @param page
     * @param searchType
     * @param principal
     *
     * 전체 리뷰 조회.
     */
    @Operation(summary = "전체 리뷰 조회",
            description = "검색은 사용자 아이디 및 닉네임 또는 상품명 기반으로 선택 후 검색. 새로운 리뷰의 기준은 답변 처리가 안된 리뷰."
    )
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "page",
                    description = "페이지 번호",
                    example = "1",
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "searchType",
                    description = "검색 타입. user 또는 product",
                    example = "user",
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "keyword",
                    description = "검색어",
                    example = "tester1",
                    in = ParameterIn.QUERY
            )
    })
    @GetMapping("/review/all")
    public ResponseEntity<PagingResponseDTO<AdminReviewDTO>> getAllReviewList(@RequestParam(name = "keyword", required = false) String keyword
                                            , @RequestParam(name = "page", required = false, defaultValue = "1") int page
                                            , @RequestParam(name = "searchType", required = false) String searchType
                                            , Principal principal) {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(keyword, searchType, page);
        PagingListDTO<AdminReviewDTO> responseDTO = adminService.getReviewList(pageDTO, AdminListType.ALL);

        return responseMappingService.mappingPagingResponseDTO(responseDTO, principal);
    }

    /**
     *
     * @param reviewId
     * @param principal
     *
     * 리뷰 상세 데이터 조회
     */
    @Operation(summary = "리뷰 상세 데이터 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "reviewId",
            description = "리뷰 아이디",
            example = "1",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/review/detail/{reviewId}")
    public ResponseEntity<ResponseDTO<AdminReviewDetailDTO>> getReviewDetail(@PathVariable("reviewId") long reviewId
                                                            , Principal principal) {
        ResponseWrappingDTO<AdminReviewDetailDTO> dto = new ResponseWrappingDTO<>(adminService.getReviewDetail(reviewId));

        return responseMappingService.mappingResponseDTO(dto, principal);
    }

    /**
     *
     * @param postDTO
     * @param principal
     *
     * 관리자의 리뷰 답변 작성
     */
    @Operation(summary = "리뷰 답변 작성")
    @DefaultApiResponse
    @SwaggerAuthentication
    @PostMapping("/review")
    public ResponseEntity<ResponseMessageDTO> postReviewReply(@RequestBody AdminReviewRequestDTO postDTO
                                                            , Principal principal) {
        String responseMessage = adminService.postReviewReply(postDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param keyword
     * @param searchType
     * @param page
     *
     * 관리자의 회원 목록 조회
     */
    @Operation(summary = "회원 목록 조회",
            description = "검색 타입으로는 아이디, 사용자 이름, 닉네임이 존재"
    )
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "page",
                    description = "페이지 번호",
                    example = "1",
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "searchType",
                    description = "검색 타입. userId, userName, nickname",
                    example = "userId",
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "keyword",
                    description = "검색어",
                    example = "tester1",
                    in = ParameterIn.QUERY
            )
    })
    @GetMapping("/member")
    public ResponseEntity<PagingResponseDTO<AdminMemberDTO>> getMember(@RequestParam(name = "keyword", required = false) String keyword
                                                                    , @RequestParam(name = "searchType", required = false) String searchType
                                                                    , @RequestParam(name = "page", required = false, defaultValue = "1") int page
                                                                    , Principal principal) {

        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(keyword, searchType, page);
        Page<AdminMemberDTO> responseDTO = adminService.getMemberList(pageDTO);

        return responseMappingService.mappingPageableResponseDTO(responseDTO, principal);
    }

    /**
     *
     * @param pointDTO
     *
     * 회원에게 포인트 직접 지급
     */
    @Operation(summary = "회원 포인트 지급")
    @DefaultApiResponse
    @SwaggerAuthentication
    @PatchMapping("/member/point")
    public ResponseEntity<ResponseMessageDTO> postPoint(@RequestBody AdminPostPointDTO pointDTO){

        String responseMessage = adminService.postPoint(pointDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param term
     *
     * 기간별 매출 조회.
     * term은 연도를 받는다.
     */
    @Operation(summary = "기간별 매출 조회",
            description = "연도별 매출 조회기능. 해당 연도의 월별 매출 데이터를 조회"
    )
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "term",
            description = "조회할 연도. 3년 전 데이터까지만 저장",
            example = "2023",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/sales/period/{term}")
    public ResponseEntity<ResponseDTO<AdminPeriodSalesResponseDTO>> getPeriodSales(@PathVariable(name = "term") int term
                                                        , Principal principal) {

        ResponseWrappingDTO<AdminPeriodSalesResponseDTO> dto = new ResponseWrappingDTO<>(adminService.getPeriodSales(term));
        return responseMappingService.mappingResponseDTO(dto, principal);
    }


    /**
     *
     * @param term
     *
     * 관리자의 월매출 조회.
     * term은 YYYY-MM으로 연월을 받는다.
     */
    @Operation(summary = "월 매출 조회",
            description = "해당 월의 매출 데이터 조회"
    )
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "term",
            description = "조회하고자 하는 연/월",
            example = "2023-04",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("sales/period/detail/{term}")
    public ResponseEntity<ResponseDTO<AdminPeriodMonthDetailResponseDTO>> getPeriodSalesDetail(@PathVariable(name = "term") String term
                                                                , Principal principal) {

        ResponseWrappingDTO<AdminPeriodMonthDetailResponseDTO> dto = new ResponseWrappingDTO<>(adminService.getPeriodSalesDetail(term));
        return responseMappingService.mappingResponseDTO(dto, principal);
    }

    /**
     *
     * @param term
     * @param classification
     *
     * 특정 상품 분류의 월 매출 조회
     * term으로 YYYY-MM 구조의 연월을 받는다.
     */
    @Operation(summary = "선택한 상품 분류의 월 매출 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "term",
                    description = "조회하고자 하는 연/월",
                    example = "2023-04",
                    required = true,
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "classification",
                    description = "조회하고자 하는 상품 분류",
                    example = "OUTER",
                    required = true,
                    in = ParameterIn.QUERY
            )
    })
    @GetMapping("/sales/period/detail/classification")
    public ResponseEntity<AdminClassificationSalesResponseDTO> getSalesByClassification(@RequestParam(value = "term")String term
                                                , @RequestParam(value = "classification") String classification) {

        AdminClassificationSalesResponseDTO responseDTO = adminService.getSalesByClassification(term, classification);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param term
     *
     * 일매출 조회.
     * term으로 YYYY-MM-DD 로 연월일을 받는다.
     */
    @Operation(summary = "일 매출 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "term",
            description = "조회하고자 하는 연/월/일",
            example = "2023-04-02",
            required = true,
            in = ParameterIn.QUERY
    )
    @GetMapping("/sales/period/detail/day")
    public ResponseEntity<AdminPeriodSalesResponseDTO> getSalesByDay(@RequestParam(value = "term") String term) {

        AdminPeriodSalesResponseDTO responseDTO = adminService.getSalesByDay(term);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param term
     * @param page
     *
     * 선택 일자의 모든 주문 목록을 조회
     * term으로 YYYY-MM-DD구조의 연월일을 받는다.
     */
    @Operation(summary = "선택일자의 모든 주문 목록 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "term",
                    description = "조회하고자 하는 연/월/일",
                    example = "2023-04-02",
                    required = true,
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "page",
                    description = "페이지 번호",
                    example = "1",
                    required = true,
                    in = ParameterIn.QUERY
            )
    })
    @GetMapping("/sales/period/order-list")
    public ResponseEntity<PagingElementsResponseDTO<AdminDailySalesResponseDTO>> getOrderListByDay(@RequestParam(value = "term") String term
                                                                        , @RequestParam(value = "page") int page
                                                                        , Principal principal) {


        PagingListDTO<AdminDailySalesResponseDTO> responseDTO = adminService.getOrderListByDay(term, page);
        return responseMappingService.mappingPagingElementsResponseDTO(responseDTO, principal);
    }


    /**
     *
     * @param keyword
     * @param page
     *
     * 상품별 매출 조회.
     * 상품 분류를 기준으로 정렬한다.
     */
    @Operation(summary = "상품별 매출 조회",
            description = "상품 분류를 기준으로 정렬"
    )
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "page",
                    description = "페이지 번호",
                    example = "1",
                    in = ParameterIn.PATH
            ),
            @Parameter(name = "keyword",
                    description = "검색어. 상품명",
                    example = "DummyOUTER",
                    in = ParameterIn.PATH
            )
    })
    @GetMapping("/sales/product")
    public ResponseEntity<PagingResponseDTO<AdminProductSalesListDTO>> getProductSales(@RequestParam(value = "keyword", required = false) String keyword
                                            , @RequestParam(value = "page", required = false, defaultValue = "1") int page
                                            , Principal principal) {

        AdminPageDTO pageDTO = new AdminPageDTO(keyword, page);

        Page<AdminProductSalesListDTO> responseDTO = adminService.getProductSalesList(pageDTO);
        return responseMappingService.mappingPageableResponseDTO(responseDTO, principal);
    }

    /**
     *
     * @param productId
     *
     * 상품 매출 상세 내역 조회
     *
     */
    @Operation(summary = "상품 매출의 상세 내역 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "productId",
            description = "상품 아이디",
            example = "BAGS20210629134401",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/sales/product/{productId}")
    public ResponseEntity<ResponseDTO<AdminProductSalesDetailDTO>> getProductSales(@PathVariable(name = "productId") String productId
                                            , Principal principal) {

        ResponseWrappingDTO<AdminProductSalesDetailDTO> responseDTO = new ResponseWrappingDTO<>(adminService.getProductSalesDetail(productId));

        return responseMappingService.mappingResponseDTO(responseDTO, principal);

    }


    /**
     *
     * @param principal
     *
     * 각 DLQ에 담긴 실패한 메시지 수량 반환.
     */
    @Operation(summary = "RabbitMQ 처리 중 실패한 메시지를 담고 있는 각 DLQ의 메시지 개수 반환. 실패 메시지가 존재하는 DLQ만 반환")
    @DefaultApiResponse
    @SwaggerAuthentication
    @GetMapping("/message")
    public ResponseEntity<ResponseListDTO<FailedQueueDTO>> getFailedQueueCount(Principal principal) {
        List<FailedQueueDTO> result = adminService.getFailedMessageList();

        return responseMappingService.mappingResponseListDTO(result, principal);
    }

    @Operation(summary = "DLQ 데이터 재시도 요청")
    @DefaultApiResponse
    @SwaggerAuthentication
    @PostMapping("/message")
    public ResponseEntity<ResponseMessageDTO> retryDLQMessages(@RequestBody List<FailedQueueDTO> failedQueueDTO) {
        /*String responseMessage = adminService.retryFailedMessages(failedQueueDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));*/

        failedQueueDTO.forEach(v -> log.info("AdminController.retryDLQMessages :: dto : {}", v));

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO("OK"));
    }
}
