package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.admin.*;
import com.example.mansshop_boot.domain.dto.cart.CartDetailDTO;
import com.example.mansshop_boot.domain.dto.cart.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.repository.ProductOrderDetailRepository;
import com.example.mansshop_boot.repository.ProductOrderRepository;
import com.example.mansshop_boot.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class AdminServiceImplTest {

    @Autowired
    private AdminService adminService;

    /*
        test 시간
        월별 매출 조회 56.396
        날짜 주문 내역 조회 3.428
        미처리 주문 조회 0.852
        연도 월별 매출 정보 조회 0.738
        연월에 대한 상세 매출 정보 2.250
        연월일의 매출 정보 조회 1.110
        상품 매출 상세 정보 0.060


        ProductOrderDetailRepository.findByOrderIds = 0.185
        ProductOrderDetailRepository.findByOrderIds = 0.186
        AdminServiceImpl.getNewOrderList = 0.849
        ProductOrderRepository.findPeriodList = 0.728
        AdminService.getPeriodSales = 0.730
        AdminService.getPeriodSalesDetail = 145.245
        AdminService.getSalesByDay = 70.641

     */

    @Autowired
    private ProductOrderDetailRepository productOrderDetailRepository;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;

    @Test
    void cartTest() {
        CartMemberDTO memberDTO = new CartMemberDTO("coco", null);
        List<CartDetailDTO> dto = cartService.getCartList(memberDTO);
        dto.forEach(System.out::println);
    }

    @Test
    @DisplayName("전달받은 연도의 월별 매출 정보를 조회")
    void getPeriodSalesList() {
        int term = 2024;

        int resultLength = 12;

        AdminPeriodSalesResponseDTO dto = adminService.getPeriodSales(term);

        Assertions.assertEquals(resultLength, dto.content().size());

        dto.content().forEach(System.out::println);

    }

    @Test
    @DisplayName("전달받은 연월에 대한 상세 매출 정보를 조회")
    void getPeriodSalesDetail() {

        /*
            1차 테스트 결과
            AdminService.getPeriodSalesDetail = 149,508ms

            개선전 테스트
            쿼리별 수행시간 체크.
            메소드 내 처리 구간을 나눠 체크.
         */
        String term = "2024-06";

        AdminPeriodMonthDetailResponseDTO content = adminService.getPeriodSalesDetail(term);
        List<AdminBestSalesProductDTO> bestProduct = content.bestProduct();
        List<AdminPeriodClassificationDTO> classificationSales = content.classificationSales();
        List<AdminPeriodSalesListDTO> dailySales = content.dailySales();

        Assertions.assertEquals(30, dailySales.size());

        System.out.println("content : " + content);
        System.out.println("-----------------------------");
        bestProduct.forEach(data -> System.out.println("bestProduct : " + data));
        System.out.println("-----------------------------");
        classificationSales.forEach(data -> System.out.println("classificationSales : " + data));
        System.out.println("-----------------------------");
        dailySales.forEach(data -> System.out.println("dailySales : " + data));
    }

    @Test
    @DisplayName("전달받은 분류의 해당 월 매출 정보를 조회")
    void getSalesByClassification() {
        String term = "2024-06";
        String classification = "OUTER";

        AdminClassificationSalesResponseDTO dto = adminService.getSalesByClassification(term, classification);
        System.out.println("content : " + dto);
        List<AdminClassificationSalesProductListDTO> productList = dto.product();
        System.out.println("-----------------------------");
        productList.forEach(System.out::println);
    }

    @Test
    @DisplayName("전달받은 연월일의 매출 정보를 조회")
    void getSalesByDay() {
        String term = "2024-06-20";

        AdminPeriodSalesResponseDTO dto = adminService.getSalesByDay(term);
        System.out.println("content : " + dto.content());
        System.out.println("-----------------------------");
        List<?> classificationList = dto.content();
        classificationList.forEach(System.out::println);
    }

    @Test
    @DisplayName("해당 날짜의 주문 내역 전체 조회. 페이징으로 처리.")
    void getOrderListByDay() {
        String term = "2024-06-20";
        int page = 1;

        PagingListDTO<AdminDailySalesResponseDTO> dto = adminService.getOrderListByDay(term, page);
        dto.content().forEach(System.out::println);

    }

    @Test
    @DisplayName("상품의 매출 상세 정보 조회")
    void getProductSalesDetail() {
        String productId = "OUTER20210630113546";

        AdminProductSalesDetailDTO response = adminService.getProductSalesDetail(productId);
        response.monthSales().forEach(v -> System.out.println("month sales : " + v));
        System.out.println("-----------------------------");
        response.optionTotalSales().forEach(v -> System.out.println("optionTotal : " + v));
        System.out.println("-----------------------------");
        response.optionYearSales().forEach(v -> System.out.println("option year : " + v));
        System.out.println("-----------------------------");
        response.optionLastYearSales().forEach(v -> System.out.println("option last year : " + v));
    }


    @Test
    @DisplayName("미처리 주문 조회")
    void newOrderList() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);

        PagingListDTO<AdminOrderResponseDTO> response = adminService.getNewOrderList(pageDTO);

        response.content().forEach(System.out::println);
    }
}