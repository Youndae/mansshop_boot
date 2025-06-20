package com.example.mansshop_boot.service.unit.admin;

import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.config.customException.exception.CustomNotFoundException;
import com.example.mansshop_boot.domain.dto.admin.business.*;
import com.example.mansshop_boot.domain.dto.admin.out.*;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.PageAmount;
import com.example.mansshop_boot.repository.periodSales.PeriodSalesSummaryRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderDetailRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderRepository;
import com.example.mansshop_boot.repository.productSales.ProductSalesSummaryRepository;
import com.example.mansshop_boot.service.admin.AdminSalesServiceImpl;
import com.example.mansshop_boot.Fixture.AdminPageDTOFixture;
import com.example.mansshop_boot.service.unit.fixture.OrderUnitFixture;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminSalesServiceUnitTest {

    @InjectMocks
    private AdminSalesServiceImpl adminSalesService;

    @Mock
    private PeriodSalesSummaryRepository periodSalesSummaryRepository;

    @Mock
    private ProductSalesSummaryRepository productSalesSummaryRepository;

    @Mock
    private ProductOrderDetailRepository productOrderDetailRepository;

    @Mock
    private ProductOrderRepository productOrderRepository;

    @Test
    @DisplayName(value = "기간별 매출 조회. 해당 연도의 월 매출 리스트")
    void getPeriodSales() {
        List<AdminPeriodSalesListDTO> adminPeriodSalesListDTOS = new ArrayList<>();
        int yearSales = 0;
        int yearSalesQuantity = 0;
        int yearOrderQuantity = 0;

        for(int i = 1; i <= 12; i++) {
            int sales = i * 1000;
            int salesQuantity = i * 2000;
            int orderQuantity = i * 3000;

            adminPeriodSalesListDTOS.add(
                    new AdminPeriodSalesListDTO(
                            i,
                            sales,
                            salesQuantity,
                            orderQuantity
                    )
            );

            yearSales += sales;
            yearSalesQuantity += salesQuantity;
            yearOrderQuantity += orderQuantity;
        }
        int term = 2025;

        when(periodSalesSummaryRepository.findPeriodList(term))
                .thenReturn(adminPeriodSalesListDTOS);

        AdminPeriodSalesResponseDTO result = assertDoesNotThrow(() -> adminSalesService.getPeriodSales(term));

        assertNotNull(result);
        assertEquals(adminPeriodSalesListDTOS, result.content());
        assertEquals(yearSales, result.sales());
        assertEquals(yearSalesQuantity, result.salesQuantity());
        assertEquals(yearOrderQuantity, result.orderQuantity());
    }

    @Test
    @DisplayName(value = "기간별 매출 조회. 해당 연도의 월 매출 리스트. 데이터가 없는 경우")
    void getPeriodSalesEmpty() {
        int term = 2025;
        List<AdminPeriodSalesListDTO> adminPeriodSalesListEmptyDTOS = IntStream.range(1, 13)
                        .mapToObj(v ->
                                new AdminPeriodSalesListDTO(v, 0, 0, 0)
                        )
                        .toList();

        when(periodSalesSummaryRepository.findPeriodList(term))
                .thenReturn(Collections.emptyList());

        AdminPeriodSalesResponseDTO result = assertDoesNotThrow(() -> adminSalesService.getPeriodSales(term));

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals(adminPeriodSalesListEmptyDTOS, result.content());
        assertEquals(0, result.sales());
        assertEquals(0, result.salesQuantity());
        assertEquals(0, result.orderQuantity());
    }

    @Test
    @DisplayName(value = "기간별 매출 월 매출 상세 조회")
    void getPeriodSalesDetail() {
        String term = "2025-01";
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = startDate.plusMonths(1);
        LocalDate lastYearStartDate = startDate.minusYears(1);
        LocalDate lastYearEndDate = endDate.minusYears(1);

        AdminPeriodSalesStatisticsDTO statisticsDTO = new AdminPeriodSalesStatisticsDTO(
                100000,
                50,
                30,
                3500,
                70000,
                30000
        );

        List<AdminBestSalesProductDTO> best5ProductList = createBest5ProductList();
        List<AdminPeriodSalesListDTO> dailySalesResponseList = createDailySalesResponseList();
        List<AdminPeriodClassificationDTO> classificationDTOList = createClassificationDTOList();
        AdminPeriodSalesStatisticsDTO lastYearStatistics = new AdminPeriodSalesStatisticsDTO(
                50000,
                25,
                15,
                0,
                35000,
                15000
        );

        when(periodSalesSummaryRepository.findPeriodStatistics(startDate, endDate))
                .thenReturn(statisticsDTO);
        when(productSalesSummaryRepository.findPeriodBestProductOrder(startDate, endDate))
                .thenReturn(best5ProductList);
        when(periodSalesSummaryRepository.findPeriodDailyList(startDate, endDate))
                .thenReturn(dailySalesResponseList);
        when(productSalesSummaryRepository.findPeriodClassification(startDate, endDate))
                .thenReturn(classificationDTOList);
        when(periodSalesSummaryRepository.findPeriodStatistics(lastYearStartDate, lastYearEndDate))
                .thenReturn(lastYearStatistics);

        AdminPeriodMonthDetailResponseDTO result = assertDoesNotThrow(() -> adminSalesService.getPeriodSalesDetail(term));

        assertNotNull(result);
        assertEquals(statisticsDTO.monthSales(), result.monthSales());
        assertEquals(statisticsDTO.monthSalesQuantity(), result.monthSalesQuantity());
        assertEquals(statisticsDTO.monthOrderQuantity(), result.monthOrderQuantity());
        assertEquals((statisticsDTO.monthSales() - lastYearStatistics.monthSales()), result.lastYearComparison());
        assertEquals(lastYearStatistics.monthSales(), result.lastYearSales());
        assertEquals(lastYearStatistics.monthSalesQuantity(), result.lastYearSalesQuantity());
        assertEquals(lastYearStatistics.monthOrderQuantity(), result.lastYearOrderQuantity());
        assertFalse(result.bestProduct().isEmpty());
        assertEquals(best5ProductList, result.bestProduct());
        assertFalse(result.classificationSales().isEmpty());
        assertEquals(classificationDTOList, result.classificationSales());
        assertFalse(result.dailySales().isEmpty());
        assertEquals(dailySalesResponseList, result.dailySales());
    }

    @Test
    @DisplayName(value = "기간별 매출 월 매출 상세 조회. 전년 동월 데이터는 있으나, 해당 월 데이터가 없는 경우")
    void getPeriodSalesDetailEmpty() {
        String term = "2025-01";
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = startDate.plusMonths(1);
        LocalDate lastYearStartDate = startDate.minusYears(1);
        LocalDate lastYearEndDate = endDate.minusYears(1);

        AdminPeriodSalesStatisticsDTO lastYearStatistics = new AdminPeriodSalesStatisticsDTO(
                50000,
                25,
                15,
                0,
                35000,
                15000
        );

        when(periodSalesSummaryRepository.findPeriodStatistics(eq(startDate), eq(endDate)))
                .thenReturn(null);
        when(productSalesSummaryRepository.findPeriodBestProductOrder(startDate, endDate))
                .thenReturn(Collections.emptyList());
        when(periodSalesSummaryRepository.findPeriodDailyList(startDate, endDate))
                .thenReturn(Collections.emptyList());
        when(productSalesSummaryRepository.findPeriodClassification(startDate, endDate))
                .thenReturn(Collections.emptyList());
        when(periodSalesSummaryRepository.findPeriodStatistics(eq(lastYearStartDate), eq(lastYearEndDate)))
                .thenReturn(lastYearStatistics);

        AdminPeriodMonthDetailResponseDTO result = assertDoesNotThrow(() -> adminSalesService.getPeriodSalesDetail(term));

        assertNotNull(result);
        assertEquals(0, result.monthSales());
        assertEquals(0, result.monthSalesQuantity());
        assertEquals(0, result.monthOrderQuantity());
        assertEquals(lastYearStatistics.monthSales() * -1, result.lastYearComparison());
        assertEquals(lastYearStatistics.monthSales(), result.lastYearSales());
        assertEquals(lastYearStatistics.monthSalesQuantity(), result.lastYearSalesQuantity());
        assertEquals(lastYearStatistics.monthOrderQuantity(), result.lastYearOrderQuantity());
        assertTrue(result.bestProduct().isEmpty());
        assertTrue(result.classificationSales().isEmpty());
        assertTrue(result.dailySales().isEmpty());
    }

    @Test
    @DisplayName(value = "기간별 매출 월 매출 상세 조회. 해당 월 데이터는 있으나, 전년 동월 데이터가 없는 경우")
    void getPeriodSalesDetailLastYearEmpty() {
        String term = "2025-01";
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = startDate.plusMonths(1);
        LocalDate lastYearStartDate = startDate.minusYears(1);
        LocalDate lastYearEndDate = endDate.minusYears(1);

        AdminPeriodSalesStatisticsDTO statisticsDTO = new AdminPeriodSalesStatisticsDTO(
                100000,
                50,
                30,
                3500,
                70000,
                30000
        );

        List<AdminBestSalesProductDTO> best5ProductList = createBest5ProductList();
        List<AdminPeriodSalesListDTO> dailySalesResponseList = createDailySalesResponseList();
        List<AdminPeriodClassificationDTO> classificationDTOList = createClassificationDTOList();

        when(periodSalesSummaryRepository.findPeriodStatistics(startDate, endDate))
                .thenReturn(statisticsDTO);
        when(productSalesSummaryRepository.findPeriodBestProductOrder(startDate, endDate))
                .thenReturn(best5ProductList);
        when(periodSalesSummaryRepository.findPeriodDailyList(startDate, endDate))
                .thenReturn(dailySalesResponseList);
        when(productSalesSummaryRepository.findPeriodClassification(startDate, endDate))
                .thenReturn(classificationDTOList);
        when(periodSalesSummaryRepository.findPeriodStatistics(lastYearStartDate, lastYearEndDate))
                .thenReturn(null);

        AdminPeriodMonthDetailResponseDTO result = assertDoesNotThrow(() -> adminSalesService.getPeriodSalesDetail(term));

        assertNotNull(result);
        assertEquals(statisticsDTO.monthSales(), result.monthSales());
        assertEquals(statisticsDTO.monthSalesQuantity(), result.monthSalesQuantity());
        assertEquals(statisticsDTO.monthOrderQuantity(), result.monthOrderQuantity());
        assertEquals(statisticsDTO.monthSales(), result.lastYearComparison());
        assertEquals(0, result.lastYearSales());
        assertEquals(0, result.lastYearSalesQuantity());
        assertEquals(0, result.lastYearOrderQuantity());
        assertFalse(result.bestProduct().isEmpty());
        assertEquals(best5ProductList, result.bestProduct());
        assertFalse(result.classificationSales().isEmpty());
        assertEquals(classificationDTOList, result.classificationSales());
        assertFalse(result.dailySales().isEmpty());
        assertEquals(dailySalesResponseList, result.dailySales());
    }

    @Test
    @DisplayName(value = "기간별 매출 월 매출 상세 조회. 해당 월 데이터, 전년 동월 데이터 모두 없는 경우")
    void getPeriodSalesDetailAllEmpty() {
        String term = "2025-01";
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = startDate.plusMonths(1);
        LocalDate lastYearStartDate = startDate.minusYears(1);
        LocalDate lastYearEndDate = endDate.minusYears(1);

        when(periodSalesSummaryRepository.findPeriodStatistics(eq(startDate), eq(endDate)))
                .thenReturn(null);
        when(productSalesSummaryRepository.findPeriodBestProductOrder(startDate, endDate))
                .thenReturn(Collections.emptyList());
        when(periodSalesSummaryRepository.findPeriodDailyList(startDate, endDate))
                .thenReturn(Collections.emptyList());
        when(productSalesSummaryRepository.findPeriodClassification(startDate, endDate))
                .thenReturn(Collections.emptyList());
        when(periodSalesSummaryRepository.findPeriodStatistics(eq(lastYearStartDate), eq(lastYearEndDate)))
                .thenReturn(null);

        AdminPeriodMonthDetailResponseDTO result = assertDoesNotThrow(() -> adminSalesService.getPeriodSalesDetail(term));

        assertNotNull(result);
        assertEquals(0, result.monthSales());
        assertEquals(0, result.monthSalesQuantity());
        assertEquals(0, result.monthOrderQuantity());
        assertEquals(0, result.lastYearComparison());
        assertEquals(0, result.lastYearSales());
        assertEquals(0, result.lastYearSalesQuantity());
        assertEquals(0, result.lastYearOrderQuantity());
        assertTrue(result.bestProduct().isEmpty());
        assertTrue(result.classificationSales().isEmpty());
        assertTrue(result.dailySales().isEmpty());
    }

    @Test
    @DisplayName(value = "상품 분류의 월 매출 내역 조회")
    void getSalesByClassification() {
        String term = "2025-01";
        String classification = "OUTER";
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = startDate.plusMonths(1);

        AdminClassificationSalesDTO classificationSalesDTO = new AdminClassificationSalesDTO(
                100000,
                500,
                250
        );

        List<AdminClassificationSalesProductListDTO> productList = IntStream.range(0, 3)
                                                                    .mapToObj(v ->
                                                                            new AdminClassificationSalesProductListDTO(
                                                                                    "productName" + v,
                                                                                    "size" + v,
                                                                                    "color" + v,
                                                                                    30000,
                                                                                    150
                                                                            )
                                                                    )
                                                                    .toList();

        when(productSalesSummaryRepository.findPeriodClassificationSales(startDate, endDate, classification))
                .thenReturn(classificationSalesDTO);
        when(productSalesSummaryRepository.findPeriodClassificationProductSales(startDate, endDate, classification))
                .thenReturn(productList);

        AdminClassificationSalesResponseDTO result = assertDoesNotThrow(() -> adminSalesService.getSalesByClassification(term, classification));

        assertNotNull(result);
        assertEquals(classification, result.classification());
        assertEquals(classificationSalesDTO.sales(), result.totalSales());
        assertEquals(classificationSalesDTO.salesQuantity(), result.totalSalesQuantity());
        assertEquals(productList.size(), result.productList().size());
        assertEquals(productList, result.productList());
    }

    @Test
    @DisplayName(value = "상품 분류의 월 매출 내역 조회. 데이터가 없는 경우")
    void getSalesByClassificationEmpty() {
        String term = "2025-01";
        String classification = "OUTER";
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = startDate.plusMonths(1);

        when(productSalesSummaryRepository.findPeriodClassificationSales(startDate, endDate, classification))
                .thenReturn(null);

        AdminClassificationSalesResponseDTO result = assertDoesNotThrow(() -> adminSalesService.getSalesByClassification(term, classification));


        assertNotNull(result);
        assertEquals(classification, result.classification());
        assertEquals(0, result.totalSales());
        assertEquals(0, result.totalSalesQuantity());
        assertTrue(result.productList().isEmpty());
    }

    @Test
    @DisplayName(value = "일 매출 조회")
    void getSalesByDay() {
        String term = "2025-01-01";
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = startDate.plusDays(1);

        AdminClassificationSalesDTO salesDTO = new AdminClassificationSalesDTO(
                100000,
                500,
                250
        );
        List<AdminPeriodClassificationDTO> classificationDTOList = createClassificationDTOList();

        when(periodSalesSummaryRepository.findDailySales(startDate))
                .thenReturn(salesDTO);
        when(productSalesSummaryRepository.findPeriodClassification(startDate, endDate))
                .thenReturn(classificationDTOList);

        AdminPeriodSalesResponseDTO result = assertDoesNotThrow(() -> adminSalesService.getSalesByDay(term));

        assertNotNull(result);
        assertEquals(salesDTO.sales(), result.sales());
        assertEquals(salesDTO.salesQuantity(), result.salesQuantity());
        assertEquals(salesDTO.orderQuantity(), result.orderQuantity());
        assertFalse(result.content().isEmpty());
        assertEquals(classificationDTOList, result.content());
    }

    @Test
    @DisplayName(value = "일 매출 조회. 일 매출 데이터가 존재하지 않는 경우")
    void getSalesByDayEmpty() {
        String term = "2025-01-01";
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = startDate.plusDays(1);

        when(periodSalesSummaryRepository.findDailySales(startDate))
                .thenReturn(null);

        AdminPeriodSalesResponseDTO result = assertDoesNotThrow(() -> adminSalesService.getSalesByDay(term));

        verify(productSalesSummaryRepository, never()).findPeriodClassification(startDate, endDate);

        assertNotNull(result);
        assertEquals(0, result.sales());
        assertEquals(0, result.salesQuantity());
        assertEquals(0, result.orderQuantity());
        assertTrue(result.content().isEmpty());
    }

    @Test
    @DisplayName(value = "선택 일자의 주문 내역 조회")
    void getOrderListByDay() {
        String term = "2025-01-01";
        int page = 1;
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDateTime startDate = LocalDateTime.of(start, LocalTime.MIN);
        LocalDateTime endDate = LocalDateTime.of(start, LocalTime.MAX);
        int amount = PageAmount.ADMIN_DAILY_ORDER_AMOUNT.getAmount();
        Pageable pageable = PageRequest.of(page - 1,
                amount,
                Sort.by("createdAt").descending()
        );

        List<Member> memberList = MemberAndAuthFixture.createDefaultMember(amount + 10).memberList();
        Product product = ProductFixture.createDefaultProductByOUTER(1).get(0);
        List<ProductOrder> productOrderList = OrderUnitFixture.createProductOrderList(memberList, product);
        List<ProductOrder> productOrderResponseList = productOrderList.stream().limit(30).toList();
        List<AdminOrderDetailListDTO> orderDetailList = new ArrayList<>();
        for(ProductOrder order : productOrderResponseList) {
            for(ProductOrderDetail orderDetail : order.getProductOrderDetailSet()) {
                orderDetailList.add(
                        new AdminOrderDetailListDTO(
                                order.getId(),
                                orderDetail.getProduct().getClassification().getId(),
                                orderDetail.getProduct().getProductName(),
                                orderDetail.getProductOption().getSize(),
                                orderDetail.getProductOption().getColor(),
                                orderDetail.getOrderDetailCount(),
                                orderDetail.getOrderDetailPrice(),
                                orderDetail.isOrderReviewStatus()
                        )
                );
            }
        }

        when(productOrderRepository.findAllByDay(startDate, endDate, pageable))
                .thenReturn(new PageImpl<>(productOrderResponseList, pageable, productOrderList.size()));
        when(productOrderDetailRepository.findByOrderIds(anyList()))
                .thenReturn(orderDetailList);

        PagingListDTO<AdminDailySalesResponseDTO> result = assertDoesNotThrow(() -> adminSalesService.getOrderListByDay(term, page));

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals(productOrderList.size(), result.pagingData().getTotalElements());
        assertEquals(2, result.pagingData().getTotalPages());

        for(int i = 0; i < productOrderResponseList.size(); i++ ) {
            ProductOrder order = productOrderResponseList.get(i);
            AdminDailySalesResponseDTO resultContent = result.content().get(i);
            assertEquals(order.getOrderTotalPrice(), resultContent.totalPrice());
            assertEquals(order.getDeliveryFee(), resultContent.deliveryFee());
            assertEquals(order.getPaymentType(), resultContent.paymentType());

            List<ProductOrderDetail> detailList = order.getProductOrderDetailSet();
            for(int j = 0; j < detailList.size(); j++) {
                ProductOrderDetail detail = detailList.get(j);
                AdminDailySalesDetailDTO detailContent = resultContent.detailList().get(j);

                assertEquals(detail.getProduct().getProductName(), detailContent.productName());
                assertEquals(detail.getProductOption().getSize(), detailContent.size());
                assertEquals(detail.getProductOption().getColor(), detailContent.color());
                assertEquals(detail.getOrderDetailCount(), detailContent.count());
                assertEquals(detail.getOrderDetailPrice(), detailContent.price());
            }
        }
    }

    @Test
    @DisplayName(value = "선택 일자의 주문 내역 조회. 데이터가 없는 경우")
    void getOrderListByDayEmpty() {
        String term = "2025-01-01";
        int page = 1;
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDateTime startDate = LocalDateTime.of(start, LocalTime.MIN);
        LocalDateTime endDate = LocalDateTime.of(start, LocalTime.MAX);
        Pageable pageable = PageRequest.of(page - 1,
                PageAmount.ADMIN_DAILY_ORDER_AMOUNT.getAmount(),
                Sort.by("createdAt").descending()
        );

        when(productOrderRepository.findAllByDay(startDate, endDate, pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));
        when(productOrderDetailRepository.findByOrderIds(anyList()))
                .thenReturn(Collections.emptyList());

        PagingListDTO<AdminDailySalesResponseDTO> result = assertDoesNotThrow(() -> adminSalesService.getOrderListByDay(term, page));

        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertEquals(0, result.pagingData().getTotalElements());
        assertEquals(0, result.pagingData().getTotalPages());
        assertTrue(result.pagingData().isEmpty());
    }

    @Test
    @DisplayName(value = "상품별 매출 조회")
    void getProductSalesList() {
        AdminPageDTO pageDTO = AdminPageDTOFixture.createDefaultAdminPageDTO();
        List<AdminProductSalesListDTO> productSalesListDTOList = IntStream.range(0, 30)
                                                                .mapToObj(v ->
                                                                        new AdminProductSalesListDTO(
                                                                                "OUTER",
                                                                                "productId" + v,
                                                                                "productName" + v,
                                                                                1000,
                                                                                100
                                                                        )
                                                                )
                                                                .toList();
        List<AdminProductSalesListDTO> salesList = productSalesListDTOList.stream()
                                                                    .limit(PageAmount.DEFAULT_AMOUNT.getAmount())
                                                                    .toList();
        Pageable pageable = PageRequest.of(pageDTO.page() - 1,
                pageDTO.amount(),
                Sort.by("classificationStep").ascending());


        when(productSalesSummaryRepository.findProductSalesList(pageDTO, pageable))
                .thenReturn(new PageImpl<>(salesList, pageable, productSalesListDTOList.size()));

        Page<AdminProductSalesListDTO> result = assertDoesNotThrow(() -> adminSalesService.getProductSalesList(pageDTO));

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(salesList.size(), result.getContent().size());
        assertEquals(productSalesListDTOList.size(), result.getTotalElements());
        assertEquals(2, result.getTotalPages());
    }

    @Test
    @DisplayName(value = "상품별 매출 조회. 데이터가 없는 경우")
    void getProductSalesListEmpty() {
        AdminPageDTO pageDTO = AdminPageDTOFixture.createDefaultAdminPageDTO();
        Pageable pageable = PageRequest.of(pageDTO.page() - 1,
                pageDTO.amount(),
                Sort.by("classificationStep").ascending());


        when(productSalesSummaryRepository.findProductSalesList(pageDTO, pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<AdminProductSalesListDTO> result = assertDoesNotThrow(() -> adminSalesService.getProductSalesList(pageDTO));

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    @DisplayName(value = "상품 매출 조회")
    void getProductSalesDetail() {
        LocalDate date = LocalDate.now();
        int year = date.getYear();
        String productId = "productId";

        AdminProductSalesDTO totalSalesDTO = new AdminProductSalesDTO("productName", 12000, 600);
        AdminSalesDTO yearSalesDTO = new AdminSalesDTO(6000, 300);
        AdminSalesDTO lastYearSalesDTO = new AdminSalesDTO(6000, 300);
        List<AdminPeriodSalesListDTO> monthSalesDTO = IntStream.range(1, 13)
                                                        .mapToObj(v ->
                                                                new AdminPeriodSalesListDTO(v, 1000, 50, 5)
                                                        )
                                                        .toList();
        List<AdminProductSalesOptionDTO> optionTotalSalesList =  createAdminProductSalesOptionDTOList(5);
        List<AdminProductSalesOptionDTO> optionYearSalesList = createAdminProductSalesOptionDTOList(2);
        List<AdminProductSalesOptionDTO> optionLastYearSalesList = createAdminProductSalesOptionDTOList(3);

        when(productSalesSummaryRepository.getProductSales(eq(productId)))
                .thenReturn(totalSalesDTO);
        when(productSalesSummaryRepository.getProductPeriodSales(year, productId))
                .thenReturn(yearSalesDTO);
        when(productSalesSummaryRepository.getProductPeriodSales(year - 1, productId))
                .thenReturn(lastYearSalesDTO);
        when(productSalesSummaryRepository.getProductMonthPeriodSales(year, productId))
                .thenReturn(monthSalesDTO);
        when(productSalesSummaryRepository.getProductOptionSales(0, productId))
                .thenReturn(optionTotalSalesList);
        when(productSalesSummaryRepository.getProductOptionSales(year, productId))
                .thenReturn(optionYearSalesList);
        when(productSalesSummaryRepository.getProductOptionSales(year - 1, productId))
                .thenReturn(optionLastYearSalesList);

        AdminProductSalesDetailDTO result = assertDoesNotThrow(() -> adminSalesService.getProductSalesDetail(productId));

        assertNotNull(result);
        assertEquals("productName", result.productName());
        assertEquals(totalSalesDTO.totalSales(), result.totalSales());
        assertEquals(totalSalesDTO.totalSalesQuantity(), result.totalSalesQuantity());
        assertEquals(yearSalesDTO.sales(), result.yearSales());
        assertEquals(yearSalesDTO.salesQuantity(), result.yearSalesQuantity());
        assertEquals(yearSalesDTO.sales() - lastYearSalesDTO.sales(), result.lastYearComparison());
        assertEquals(lastYearSalesDTO.sales(), result.lastYearSales());
        assertEquals(lastYearSalesDTO.salesQuantity(), result.lastYearSalesQuantity());
        assertFalse(result.monthSales().isEmpty());
        assertEquals(monthSalesDTO.size(), result.monthSales().size());
        assertFalse(result.optionTotalSales().isEmpty());
        assertEquals(optionTotalSalesList.size(), result.optionTotalSales().size());
        assertFalse(result.optionYearSales().isEmpty());
        assertEquals(optionYearSalesList.size(), result.optionYearSales().size());
        assertFalse(result.optionLastYearSales().isEmpty());
        assertEquals(optionLastYearSalesList.size(), result.optionLastYearSales().size());
    }

    @Test
    @DisplayName(value = "상품 매출 조회. 데이터가 없는 경우")
    void getProductSalesDetailEmpty() {
        String productId = "productId";

        when(productSalesSummaryRepository.getProductSales(productId))
                .thenReturn(null);

        assertThrows(
                CustomNotFoundException.class,
                () -> adminSalesService.getProductSalesDetail(productId)
        );
    }

    private List<AdminBestSalesProductDTO> createBest5ProductList() {
        return IntStream.range(0, 5)
                .mapToObj(v -> new AdminBestSalesProductDTO(
                        "productName" + v,
                        10000,
                        5000
                ))
                .toList();
    }

    private List<AdminPeriodSalesListDTO> createDailySalesResponseList () {
        return IntStream.range(1, 32)
                .mapToObj(v -> new AdminPeriodSalesListDTO(
                        v,
                        10000,
                        10,
                        5
                ))
                .toList();
    }

    private List<String> createClassificationNameList () {
        return List.of("OUTER", "TOP", "PANTS", "SHOES", "BAGS");
    }

    private List<AdminPeriodClassificationDTO> createClassificationDTOList () {
        List<String> classificationName = createClassificationNameList();
        return classificationName.stream()
                .map(v ->
                        new AdminPeriodClassificationDTO(v, 1000, 10)
                )
                .toList();
    }

    private List<AdminProductSalesOptionDTO> createAdminProductSalesOptionDTOList(int count) {
        return IntStream.range(0, count)
                .mapToObj(v ->
                        new AdminProductSalesOptionDTO(
                                v,
                                "size" + v,
                                "color" + v,
                                100,
                                10
                        )
                )
                .toList();
    }
}
