package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.admin.*;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseIdDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseListDTO;
import com.example.mansshop_boot.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
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

    @GetMapping("/product/stock/{keyword}/{page}")
    public ResponseEntity<?> getProductStock(@PathVariable(name = "keyword", required = false) String keyword
                                            , @PathVariable(name = "page") int page) {

        /*
            재고 탭
            content: [
                상품 데이터,
                optionList : [
                    ...
                ]
            ]
         */

        return null;
    }

    @GetMapping("/product/discount")
    public ResponseEntity<?> getDiscountProductList() {

        /*
            할인중인 상품들의 리스트 반환
         */

        return null;
    }

    @PatchMapping("/product/discount")
    public ResponseEntity<?> patchDiscountProduct() {

        /*
            ProductIdList, 할인율을 받고 해당 상품들의 할인율을 수정.
         */

        return null;
    }


    @GetMapping("/product/classification")
    public ResponseEntity<ResponseListDTO<String>> getProductClassification(Principal principal) {

        ResponseListDTO<String> responseDTO = adminService.getClassification(principal);

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

    @GetMapping("/order/{type}/{keyword}/{page}")
    public ResponseEntity<?> getNewOrder(@PathVariable(name = "type") String listType
                                        , @PathVariable(name = "keyword", required = false) String keyword
                                        , @PathVariable(name = "page") int page) {

        /*
            type = new -> 미처리 주문
            type = all -> 전체 주문
         */

        return null;
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetail(@PathVariable(name = "orderId") long orderId) {

        /*
            주문 정보 요청
         */

        return null;
    }

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
