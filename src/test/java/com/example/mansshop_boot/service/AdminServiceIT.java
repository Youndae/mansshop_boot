package com.example.mansshop_boot.service;

import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.admin.business.AdminReviewDTO;
import com.example.mansshop_boot.domain.dto.admin.out.*;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.enumuration.AdminListType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
//@ActiveProfiles("test")
public class AdminServiceIT {

    @Autowired
    private AdminService adminService;

    private static final String PRODUCT_ID = "BAGS20250301214630690018433";

    @Test
    @DisplayName(value = "관리자 상품 리스트 조회")
    void adminProductList() {
        AdminPageDTO pageDTO = new AdminPageDTO(null, 1);

        PagingListDTO<AdminProductListDTO> result = adminService.getProductList(pageDTO);
        Assertions.assertNotNull(result.content());
        Assertions.assertEquals(pageDTO.amount(), result.content().size());
    }

    @Test
    @DisplayName(value = "상품 분류 리스트 조회")
    void getClassificationList() {
        List<String> result = adminService.getClassification();
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName(value = "상품 상세 정보 조회")
    void getProductDetail() {

        AdminProductDetailDTO result = adminService.getProductDetail(PRODUCT_ID);
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName(value = "상품 수정 데이터 요청")
    void getPatchProductData() {
        AdminProductPatchDataDTO result = adminService.getPatchProductData(PRODUCT_ID);
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName(value = "상품 재고 리스트 반환")
    void getProductStock() {
        AdminPageDTO pageDTO = new AdminPageDTO(null, 1);
        PagingListDTO<AdminProductStockDTO> result = adminService.getProductStock(pageDTO);
        Assertions.assertNotNull(result.content());
        Assertions.assertEquals(pageDTO.amount(), result.content().size());
    }

    @Test
    @DisplayName(value = "선택한 상품 분류 중 할인 중인 상품 리스트 반환")
    void getDiscountProductList() {
        List<AdminDiscountProductDTO> result = adminService.getSelectDiscountProduct("OUTER");
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName(value = "모든 주문 목록 조회")
    void getAllOrderList() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);
        PagingListDTO<AdminOrderResponseDTO> result = adminService.getAllOrderList(pageDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(pageDTO.amount(), result.content().size());
    }

    @Test
    @DisplayName(value = "새로운 주문 목록 조회")
    void getNewOrderList() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);
        PagingListDTO<AdminOrderResponseDTO> result = adminService.getNewOrderList(pageDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(pageDTO.amount(), result.content().size());
    }

    @Test
    @DisplayName(value = "상품 문의 전체 리스트 조회")
    void getProductQnAAllList() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, "all", 1);
        PagingListDTO<AdminQnAListResponseDTO> result = adminService.getProductQnAList(pageDTO);

        Assertions.assertNotNull(pageDTO);
        Assertions.assertEquals(pageDTO.amount(), result.content().size());
    }

    @Test
    @DisplayName(value = "상품 문의 미처리 리스트 조회")
    void getProductQnANewList() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, "new", 1);
        PagingListDTO<AdminQnAListResponseDTO> result = adminService.getProductQnAList(pageDTO);

        Assertions.assertNotNull(pageDTO);
        Assertions.assertEquals(pageDTO.amount(), result.content().size());
    }

    @Test
    @DisplayName(value = "회원 문의 전체 리스트 조회")
    void getMemberQnAAllList() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, "all", 1);
        PagingListDTO<AdminQnAListResponseDTO> result = adminService.getMemberQnAList(pageDTO);

        Assertions.assertNotNull(pageDTO);
        Assertions.assertEquals(pageDTO.amount(), result.content().size());
    }

    @Test
    @DisplayName(value = "회원 문의 미처리 리스트 조회")
    void getMemberQnANewList() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, "new", 1);
        PagingListDTO<AdminQnAListResponseDTO> result = adminService.getMemberQnAList(pageDTO);

        Assertions.assertNotNull(pageDTO);
        Assertions.assertEquals(pageDTO.amount(), result.content().size());
    }

    @Test
    @DisplayName(value = "문의 분류 리스트 조회")
    void getQnAClassification() {
        List<AdminQnAClassificationDTO> result = adminService.getQnAClassification();

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName(value = "전체 리뷰 리스트")
    void getReviewAllList() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);
        PagingListDTO<AdminReviewDTO> result = adminService.getReviewList(pageDTO, AdminListType.ALL);
        result.content().forEach(System.out::println);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(pageDTO.amount(), result.content().size());
    }

    @Test
    @DisplayName(value = "미처리 리뷰 리스트")
    void getReviewNewList() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);
        PagingListDTO<AdminReviewDTO> result = adminService.getReviewList(pageDTO, AdminListType.NEW);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(pageDTO.amount(), result.content().size());
    }

    @Test
    @DisplayName(value = "리뷰 상세 조회")
    void getReviewDetail() {
        long reviewId = 2823756L;

        AdminReviewDetailDTO result = adminService.getReviewDetail(reviewId);

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName(value = "회원 리스트 조회")
    void getMemberList() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, "userId", 1);
        Page<AdminMemberDTO> result = adminService.getMemberList(pageDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(pageDTO.amount(), result.getContent().size());
    }

    @Test
    @DisplayName(value = "특정 연도의 월별 매출 집계 조회")
    void getPeriodSales() {
        int term = 2024;
        AdminPeriodSalesResponseDTO result = adminService.getPeriodSales(2024);

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName(value = "선택한 연/월의 매출 조회")
    void getPeriodSalesDetail() {
        String term = "2024-03";
        AdminPeriodMonthDetailResponseDTO result = adminService.getPeriodSalesDetail(term);

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName(value = "선택한 상품 분류의 연/월 매출 조회")
    void getSalesByClassification() {
        String term = "2024-03";
        String classification = "OUTER";
        AdminClassificationSalesResponseDTO result = adminService.getSalesByClassification(term, classification);

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName(value = "선택한 연/월/일의 매출 조회")
    void getSalesByDay() {
        String term = "2024-03-01";
        AdminPeriodSalesResponseDTO result = adminService.getSalesByDay(term);

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName(value = "선택한 연/월/일의 주문 내역 조회")
    void getOrderListByDay() {
        String term = "2024-03-01";
        int page = 1;

        PagingListDTO<AdminDailySalesResponseDTO> result = adminService.getOrderListByDay(term, page);

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName(value = "상품별 매출 조회")
    void getProductSalesList() {
        AdminPageDTO pageDTO = new AdminPageDTO(null, 1);
        Page<AdminProductSalesListDTO> result = adminService.getProductSalesList(pageDTO);

        result.getContent().forEach(System.out::println);

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName(value = "선택한 상품의 매출 조회")
    void getProductSalesDetail() {
        AdminProductSalesDetailDTO result = adminService.getProductSalesDetail(PRODUCT_ID);

        Assertions.assertNotNull(result);
    }
}
