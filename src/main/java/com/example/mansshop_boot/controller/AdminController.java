package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.admin.*;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.response.*;
import com.example.mansshop_boot.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Enumeration;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
//@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/product")
    public ResponseEntity<PagingResponseDTO<AdminProductListDTO>> getProductList(@RequestParam(name = "keyword", required = false) String keyword
                                            , @RequestParam(name = "page") int page){
        /*
            admin/product의 리스트 조회
         */

        PagingResponseDTO<AdminProductListDTO> responseDTO = adminService.getProductList(new AdminPageDTO(keyword, page));

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @GetMapping("/product/detail/{productId}")
    public ResponseEntity<?> getProductDetail(@PathVariable(name = "productId") String productId){

        /*
            admin/product/{productId}
         */

        ResponseDTO<AdminProductDetailDTO> responseDTO = adminService.getProductDetail(productId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @PostMapping(value = "/product", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> postProduct(@ModelAttribute AdminProductPatchDTO postDTO
                                        , @ModelAttribute AdminProductImageDTO imageDTO) {

        /*
            @RequestBody dto

            상품 추가

            image에 대해 AdminProductImageDTO로 받는게 가능한지 테스트.
         */

        ResponseIdDTO<String> responseDTO = adminService.postProduct(postDTO, imageDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @GetMapping("/product/patch/{productId}")
    public ResponseEntity<AdminProductPatchDataDTO> getPatchProductData(@PathVariable(name = "productId") String productId
                                                                                            , Principal principal) {

        AdminProductPatchDataDTO responseDTO = adminService.getPatchProductData(productId, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @PatchMapping(value = "/product/{productId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> patchProduct(@PathVariable(name = "productId") String productId
                                        , @RequestParam(value = "deleteOptionList", required = false) List<Long> deleteOptionList
                                        , @ModelAttribute AdminProductPatchDTO patchDTO
                                        , @ModelAttribute AdminProductImageDTO imageDTO) {



        ResponseIdDTO<String> responseDTO = adminService.patchProduct(productId, deleteOptionList, patchDTO, imageDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @GetMapping("/product/stock")
    public ResponseEntity<PagingResponseDTO<AdminProductStockDTO>> getProductStock(@RequestParam(name = "keyword", required = false) String keyword
                                            , @RequestParam(name = "page") int page) {

        AdminPageDTO pageDTO = new AdminPageDTO(keyword, page);

        PagingResponseDTO<AdminProductStockDTO> responseDTO = adminService.getProductStock(pageDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @GetMapping("/product/discount")
    public ResponseEntity<PagingResponseDTO<AdminDiscountResponseDTO>> getDiscountProductList(@RequestParam(name = "keyword", required = false) String keyword
                                                    , @RequestParam(name = "page") int page) {

        /*
            할인중인 상품들의 리스트 반환
         */

        AdminPageDTO pageDTO = new AdminPageDTO(keyword, page);

        PagingResponseDTO<AdminDiscountResponseDTO> responseDTO = adminService.getDiscountProduct(pageDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @GetMapping("/product/discount/classification")
    public ResponseEntity<?> getDiscountClassification() {
        /*
            List<String> classification
         */

        ResponseListDTO<String> responseDTO = adminService.getClassification();

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @GetMapping("/product/discount/select/{classification}")
    public ResponseEntity<ResponseListDTO<AdminDiscountProductDTO>> getDiscountProductSelectList(@PathVariable(name = "classification") String classification) {

        /*
            ProductId
            productName
         */

        ResponseListDTO<AdminDiscountProductDTO> responseDTO = adminService.getSelectDiscountProduct(classification);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @PatchMapping(value = "/product/discount", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseMessageDTO> patchDiscountProduct(@RequestBody AdminDiscountPatchDTO patchDTO) {

        ResponseMessageDTO responseDTO = adminService.patchDiscountProduct(patchDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }


    @GetMapping("/product/classification")
    public ResponseEntity<ResponseListDTO<String>> getProductClassification() {

        ResponseListDTO<String> responseDTO = adminService.getClassification();

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @PostMapping("/product/classification")
    public ResponseEntity<?> postProductClassification() {

        /*
            상품 분류 추가 및 제거.
            제거를 감안해서 처리해야 함.
         */

        return null;
    }

    @GetMapping("/order/{type}")
    public ResponseEntity<PagingResponseDTO<AdminOrderResponseDTO>> getNewOrder(@PathVariable(name = "type") String listType
                                                                                , @RequestParam(name = "searchType", required = false) String searchType
                                                                                , @RequestParam(name = "keyword", required = false) String keyword
                                                                                , @RequestParam(name = "page", required = false) int page) {

        /*
            type = new -> 미처리 주문
            type = all -> 전체 주문
         */

        /*
            data

            content : [
                {
                    orderId
                    recipient
                    userId
                    phone
                    createdAt
                    address
                    detailList : [
                        {
                            (OrderDetail join productOption, product)
                            classification
                            productName
                            size
                            color
                            count
                            price
                            reviewStatus
                        },
                        ...
                    ]
                },
                ...
                pagingData
            ]
         */

        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(keyword, searchType, page);

        PagingResponseDTO<AdminOrderResponseDTO> responseDTO;

        if(listType.equals("all"))
            responseDTO = adminService.getAllOrderList(pageDTO);
        else if(listType.equals("new"))
            responseDTO = adminService.getNewOrderList(pageDTO);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /*@GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetail(@PathVariable(name = "orderId") long orderId) {

        *//*
            주문 정보 요청
         *//*

        return null;
    }*/

    @PatchMapping("/order/{orderId}")
    public ResponseEntity<?> patchOrder(@PathVariable(name = "orderId") long orderId) {

        /*
            주문 처리.
            상품 준비중으로 변경
         */

        return null;
    }

    @GetMapping("/qna/product/{keyword}/{page}")
    public ResponseEntity<?> getProductQnA(@PathVariable(name = "keyword", required = false) String keyword
                                        , @PathVariable(name = "page") int page) {

        /*
            상품 문의 리스트
         */

        return null;
    }

    @GetMapping("/qna/product/{qnaId}")
    public ResponseEntity<?> getProductDetail(@PathVariable(name = "qnaId") long qnaId) {

        /*
            상품 문의 상세
         */

        return null;
    }

    @PatchMapping("/qna/product/{qnaId}")
    public ResponseEntity<?> patchProductQnAComplete(@PathVariable(name = "qnaId") long qnaId) {

        /*
            상품 문의 답변 완료 처리
         */

        return null;
    }

    @PostMapping("/qna/product/reply")
    public ResponseEntity<?> postProductQnAReply() {

        /*
            상품 문의 답변 작성
            qnaId, content를 받음.
         */

        return null;
    }

    @GetMapping("/qna/member/{keyword}/{page}")
    public ResponseEntity<?> getMemberQnA(@PathVariable(name = "keyword", required = false) String keyword
                                            , @PathVariable(name = "page") int page) {

        /*
            회원 문의 리스트
         */

        return null;
    }

    @GetMapping("/qna/member/{qnaId}")
    public ResponseEntity<?> getMemberDetail(@PathVariable(name = "qnaId") long qnaId) {

        /*
            회원 문의 상세
         */

        return null;
    }

    @PatchMapping("/qna/member/{qnaId}")
    public ResponseEntity<?> patchMemberQnAComplete(@PathVariable(name = "qnaId") long qnaId) {

        /*
            회원 문의 답변 완료 처리
         */

        return null;
    }

    @PostMapping("/qna/member/reply")
    public ResponseEntity<?> postMemberQnAReply() {

        /*
            회원 문의 답변 작성
            qnaId, content를 받음.

            처리 이전 사용자가 답변을 작성하는 경우 status를 0으로 바꾸도록 먼저 수정해놓고 처리.
         */

        return null;
    }

    @PostMapping("/qna/classification")
    public ResponseEntity<?> postQnAClassification() {

        /*
            문의 분류 추가 및 제거.
            제거를 감안해서 처리해야 함.
         */

        return null;
    }

    @GetMapping("/member/{keyword}/{page}")
    public ResponseEntity<?> getMember(@PathVariable(name = "keyword", required = false) String keyword
                                        , @PathVariable(name = "page") int page) {

        /*
            회원 목록
         */

        return null;
    }

    @GetMapping("/member/{userId}")
    public ResponseEntity<?> getMemberInfo(@PathVariable(name = "userId") String userId) {

        /*
            회원 상세 정보
         */

        return null;
    }

    @GetMapping("/sales/period/{page}/{term}")
    public ResponseEntity<?> getPeriodSales(@PathVariable(name = "page") int page
                                            , @PathVariable(name = "term") String term) {

        /*
            기간별 매출.
            term 값으로는
            day, month, year가 전달.

         */

        return null;
    }

    @GetMapping("/sales/period/{date}")
    public ResponseEntity<?> getPeriodSalesDetail(@PathVariable(name = "date") String date) {

        /*
            어떻게 처리할지 보류상태.
            전달되는 쿼리 스트링도 변경될수 있음.
         */

        return null;
    }

    @GetMapping("/sales/product/{keyword}/{page}")
    public ResponseEntity<?> getProductSales(@PathVariable(name = "keyword", required = false) String keyword
                                            , @PathVariable(name = "page") int page) {

        /*
            상품별 매출
         */

        return null;
    }

    @GetMapping("/sales/product/{productId}")
    public ResponseEntity<?> getProductSales(@PathVariable(name = "productId") String productId) {

        /*
            상품 매출 상세
         */

        return null;
    }
}