package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.admin.*;
import com.example.mansshop_boot.domain.dto.mypage.qna.MemberQnADetailDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.ProductQnADetailDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.req.QnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.req.QnAReplyInsertDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.response.*;
import com.example.mansshop_boot.service.AdminService;
import com.example.mansshop_boot.service.MyPageService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
//@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    private final MyPageService myPageService;

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

    @PatchMapping("/order/{orderId}")
    public ResponseEntity<?> patchOrder(@PathVariable(name = "orderId") long orderId) {

        /*
            주문 처리.
            상품 준비중으로 변경
         */

        return null;
    }

    @GetMapping("/qna/product")
    public ResponseEntity<?> getProductQnA(@RequestParam(name = "keyword", required = false) String keyword
                                        , @RequestParam(name = "page") int page
                                        , @RequestParam(name = "type") String listType) {

        /*
            상품 문의 리스트
         */

        AdminPageDTO pageDTO = new AdminPageDTO(keyword, page);

        PagingResponseDTO<AdminQnAListResponseDTO> responseDTO = adminService.getProductQnAList(pageDTO, listType);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @GetMapping("/qna/product/{qnaId}")
    public ResponseEntity<?> getProductDetail(@PathVariable(name = "qnaId") long qnaId) {

        /*
            상품 문의 상세
         */

        ProductQnADetailDTO responseDTO = adminService.getProductQnADetail(qnaId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @PatchMapping("/qna/product/{qnaId}")
    public ResponseEntity<?> patchProductQnAComplete(@PathVariable(name = "qnaId") long qnaId) {

        /*
            상품 문의 답변 완료 처리
         */

        String responseMessage = adminService.patchProductQnAComplete(qnaId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    @PostMapping("/qna/product/reply")
    public ResponseEntity<?> postProductQnAReply(@RequestBody QnAReplyInsertDTO insertDTO, Principal principal) {

        /*
            상품 문의 답변 작성
            qnaId, content를 받음.
         */

        String responseMessage = adminService.postProductQnAReply(insertDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    @PatchMapping("/qna/product/reply")
    public ResponseEntity<ResponseMessageDTO> patchProductQnAReply(@RequestBody QnAReplyDTO replyDTO, Principal principal) {

        String responseMessage = myPageService.patchProductQnAReply(replyDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    @GetMapping("/qna/member")
    public ResponseEntity<?> getMemberQnA(@RequestParam(name = "keyword", required = false) String keyword
                                        , @RequestParam(name = "page") int page
                                        , @RequestParam(name = "type") String listType) {

        /*
            회원 문의 리스트
         */

        AdminPageDTO pageDTO = new AdminPageDTO(keyword, page);

        PagingResponseDTO<AdminQnAListResponseDTO> responseDTO = adminService.getMemberQnAList(pageDTO, listType);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @GetMapping("/qna/member/{qnaId}")
    public ResponseEntity<?> getMemberDetail(@PathVariable(name = "qnaId") long qnaId) {

        /*
            회원 문의 상세
         */

        MemberQnADetailDTO responseDTO = adminService.getMemberQnADetail(qnaId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @PatchMapping("/qna/member/{qnaId}")
    public ResponseEntity<?> patchMemberQnAComplete(@PathVariable(name = "qnaId") long qnaId) {

        /*
            회원 문의 답변 완료 처리
         */

        String responseMessage = adminService.patchMemberQnAComplete(qnaId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    @PostMapping("/qna/member/reply")
    public ResponseEntity<?> postMemberQnAReply(@RequestBody QnAReplyInsertDTO insertDTO, Principal principal) {

        /*
            회원 문의 답변 작성
            qnaId, content를 받음.

            처리 이전 사용자가 답변을 작성하는 경우 status를 0으로 바꾸도록 먼저 수정해놓고 처리.
         */

        String responseMessage = adminService.postMemberQnAReply(insertDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    @PatchMapping("/qna/member/reply")
    public ResponseEntity<ResponseMessageDTO> patchMemberQnAReply(@RequestBody QnAReplyDTO replyDTO, Principal principal) {

        String responseMessage = myPageService.patchMemberQnAReply(replyDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    @GetMapping("/qna/classification")
    public ResponseEntity<?> getQnAClassification() {

        /*
            문의 분류 추가 및 제거.
            제거를 감안해서 처리해야 함.
         */

        ResponseListDTO<AdminQnAClassificationDTO> responseDTO = adminService.getQnAClassification();

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @PostMapping("/qna/classification")
    public ResponseEntity<?> postQnAClassification(@RequestBody Map<String, String> classification) {

        /*
            문의 분류 추가 및 제거.
            제거를 감안해서 처리해야 함.
         */

        String responseMessage = adminService.postQnAClassification(classification.get("name"));

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    @DeleteMapping("/qna/classification/{qnaClassificationId}")
    public ResponseEntity<?> deleteQnAClassification(@PathVariable(name = "qnaClassificationId") Long classificationId) {

        /*
            문의 분류 추가 및 제거.
            제거를 감안해서 처리해야 함.
         */

        String responseMessage = adminService.deleteQnAClassification(classificationId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    @GetMapping("/member")
    public ResponseEntity<PagingResponseDTO<AdminMemberDTO>> getMember(@RequestParam(name = "keyword", required = false) String keyword
                                        , @RequestParam(name = "page") int page) {

        /*
            회원 목록
         */

        AdminPageDTO pageDTO = new AdminPageDTO(keyword, page);

        PagingResponseDTO<AdminMemberDTO> responseDTO = adminService.getMemberList(pageDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @GetMapping("/member/{userId}")
    public ResponseEntity<?> getMemberInfo(@PathVariable(name = "userId") String userId) {

        /*
            회원 상세 정보
         */

        return null;
    }

    /*
        기간별 매출 리스트
        연도별 출력으로 처리한다.
        해당 연도의 모든 월 매출을 리스트화 해서 응답한다.
     */
    @GetMapping("/sales/period/{term}")
    public ResponseEntity<ResponseDTO<AdminPeriodSalesResponseDTO>> getPeriodSales(@PathVariable(name = "term") int term) {


        ResponseDTO<AdminPeriodSalesResponseDTO> responseDTO = adminService.getPeriodSales(term);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /*
        기간별 상세 페이지
        해당월의 매출 정보를 전달.
        YYYY-MM 형태로 Request
     */

    @GetMapping("sales/period/detail/{term}")
    public ResponseEntity<ResponseDTO<AdminPeriodMonthDetailResponseDTO>> getPeriodSalesDetail(@PathVariable(name = "term") String term) {

        ResponseDTO<AdminPeriodMonthDetailResponseDTO> responseDTO = adminService.getPeriodSalesDetail(term);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /*
        상품 분류별 월 매출 데이터 조회
        해당 기간의 상품 분류에 해당하는 데이터의 상품명, 옵션, 수량, 금액을 전달한다.
     */
    @GetMapping("/sales/period/detail/classification")
    public ResponseEntity<AdminClassificationSalesResponseDTO> getSalesByClassification(@RequestParam(value = "term")String term
                                                , @RequestParam(value = "classification") String classification) {

        AdminClassificationSalesResponseDTO responseDTO = adminService.getSalesByClassification(term, classification);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /*
        일별 매출 데이터 조회
        해당 일의 매출 데이터를 전달.
     */
    @GetMapping("/sales/period/detail/day")
    public ResponseEntity<AdminPeriodSalesResponseDTO> getSalesByDay(@RequestParam(value = "term") String term) {

        /*
            totalSales
            totalSalesQuantity
            totalOrderQuantity

            List<?> classificationSalesList : [
                {
                    classification
                    classificationSales
                    classificationSalesQuantity
                },
                ...
            ]
         */

        AdminPeriodSalesResponseDTO responseDTO = adminService.getSalesByDay(term);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /*
        특정 날짜의 모든 주문 내역 조회
        페이징으로 처리한다.
        term으로 기간을 받는다.
        단순하게 모든 주문 내역을 응답한다.
     */
    @GetMapping("/sales/period/order-list")
    public ResponseEntity<PagingElementsResponseDTO<AdminDailySalesResponseDTO>> getOrderListByDay(@RequestParam(value = "term") String term
                                                                                        , @RequestParam(value = "page") int page) {

        /*
            paging

            content : [
                orderTotalPrice
                deliveryFee
                paymentType
                detailList: [
                    {
                        productName
                        size
                        color
                        count
                        price
                    }
                ]
            ]
         */

        PagingElementsResponseDTO<AdminDailySalesResponseDTO> responseDTO = adminService.getOrderListByDay(term, page);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }


    @GetMapping("/sales/product/{keyword}/{page}")
    public ResponseEntity<?> getProductSales(@PathVariable(name = "keyword", required = false) String keyword
                                            , @PathVariable(name = "page") int page) {

        /*
            상품별 매출

            classification
            productId
            productName
            sales
            salesQuantity

         */

        AdminPageDTO pageDTO = new AdminPageDTO(keyword, page);

        PagingElementsResponseDTO<AdminProductSalesListDTO> responseDTO = adminService.getProductSalesList(pageDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @GetMapping("/sales/product/{productId}")
    public ResponseEntity<?> getProductSales(@PathVariable(name = "productId") String productId) {

        /*
            상품 매출 상세

            productName
            sales
            salesQuantity
            lastYearComparison
            lastYearSales
            lastYearSalesQuantity
            List<?> termList
                term
                sales
                salesQuantity
                orderQuantity
            List<?> optionList
                size
                color
                optionSales
                optionSalesQuantity
         */

        ResponseDTO<AdminProductSalesDetailDTO> responseDTO = adminService.getProductSalesDetail(productId);


        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }
}
