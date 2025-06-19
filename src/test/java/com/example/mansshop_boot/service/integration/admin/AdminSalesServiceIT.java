package com.example.mansshop_boot.service.integration.admin;

import com.example.mansshop_boot.Fixture.*;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.Fixture.util.PaginationUtils;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.config.customException.exception.CustomNotFoundException;
import com.example.mansshop_boot.domain.dto.admin.business.*;
import com.example.mansshop_boot.domain.dto.admin.out.*;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.PageAmount;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.periodSales.PeriodSalesSummaryRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderDetailRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderRepository;
import com.example.mansshop_boot.repository.productSales.ProductSalesSummaryRepository;
import com.example.mansshop_boot.service.admin.AdminSalesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
@ActiveProfiles("test")
@Transactional
public class AdminSalesServiceIT {

    @Autowired
    private AdminSalesService adminSalesService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClassificationRepository classificationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private ProductOrderDetailRepository productOrderDetailRepository;

    @Autowired
    private PeriodSalesSummaryRepository periodSalesSummaryRepository;

    @Autowired
    private ProductSalesSummaryRepository productSalesSummaryRepository;

    private int TERM_YEAR = 2024;

    private String TERM_MONTH = "2024-01";

    private String TERM = "2024-01-01";

    private List<ProductSalesSummary> productSalesSummaryList;

    private List<PeriodSalesSummary> periodSalesSummaryList;

    private List<Classification> classificationList;

    private List<Product> productList;

    private List<ProductOption> productOptionList;

    private List<ProductOrder> orderList;

    @BeforeEach
    void init() {
        MemberAndAuthFixtureDTO memberAndAuthFixture = MemberAndAuthFixture.createDefaultMember(10);
        List<Member> memberList = memberAndAuthFixture.memberList();
        memberRepository.saveAll(memberList);
        classificationList = ClassificationFixture.createClassification();
        classificationRepository.saveAll(classificationList);
        productList = ProductFixture.createSaveProductList(30, classificationList.get(0));
        productRepository.saveAll(productList);
        productOptionList = productList.stream()
                .flatMap(v ->
                        v.getProductOptions().stream()
                )
                .toList();
        productOptionRepository.saveAll(productOptionList);

        orderList = ProductOrderFixture.createDefaultProductOrder(memberList, productOptionList);
        productOrderRepository.saveAll(orderList);

        productSalesSummaryList = SalesSummaryFixture.createProductSalesSummary(productList);
        periodSalesSummaryList = SalesSummaryFixture.createPeriodSalesSummary();
        productSalesSummaryRepository.saveAll(productSalesSummaryList);
        periodSalesSummaryRepository.saveAll(periodSalesSummaryList);
    }

    @Test
    @DisplayName(value = "기간별 매출 조회")
    void getPeriodSales() {
        long yearSales = 0L;
        long yearSalesQuantity = 0L;
        long yearOrderSalesQuantity = 0L;
        Map<Integer, AdminPeriodSalesListDTO> fixtureMap = new HashMap<>();

        for(PeriodSalesSummary summary : periodSalesSummaryList) {
            if(summary.getPeriod().getYear() == TERM_YEAR) {
                int month = summary.getPeriod().getMonthValue();
                AdminPeriodSalesListDTO mapDTO = fixtureMap.getOrDefault(month, new AdminPeriodSalesListDTO(month));

                AdminPeriodSalesListDTO putDTO = new AdminPeriodSalesListDTO(
                        month,
                        mapDTO.sales() + summary.getSales(),
                        mapDTO.salesQuantity() + summary.getSalesQuantity(),
                        mapDTO.orderQuantity() + summary.getOrderQuantity()
                );

                fixtureMap.put(month, putDTO);
                yearSales += summary.getSales();
                yearSalesQuantity += summary.getSalesQuantity();
                yearOrderSalesQuantity += summary.getOrderQuantity();
            }
        }

        AdminPeriodSalesResponseDTO<AdminPeriodSalesListDTO> result = Assertions.assertDoesNotThrow(() -> adminSalesService.getPeriodSales(TERM_YEAR));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertEquals(yearSales, result.sales());
        Assertions.assertEquals(yearSalesQuantity, result.salesQuantity());
        Assertions.assertEquals(yearOrderSalesQuantity, result.orderQuantity());

        for(AdminPeriodSalesListDTO content : result.content()) {
            AdminPeriodSalesListDTO mapData = fixtureMap.get(content.date());

            Assertions.assertEquals(mapData.sales(), content.sales());
            Assertions.assertEquals(mapData.salesQuantity(), content.salesQuantity());
            Assertions.assertEquals(mapData.orderQuantity(), content.orderQuantity());
        }
    }

    @Test
    @DisplayName(value = "기간별 매출 조회. 데이터가 없는 경우")
    void getPeriodSalesEmpty() {
        AdminPeriodSalesResponseDTO<AdminPeriodSalesListDTO> result = Assertions.assertDoesNotThrow(() -> adminSalesService.getPeriodSales(2000));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());

        for(AdminPeriodSalesListDTO content : result.content()) {
            Assertions.assertEquals(0, content.sales());
            Assertions.assertEquals(0, content.salesQuantity());
            Assertions.assertEquals(0, content.orderQuantity());
        }
    }

    @Test
    @DisplayName(value = "기간별 매출 상세 조회.")
    void getPeriodSalesDetail() {
        String[] termSplit = TERM_MONTH.split("-");
        int year = Integer.parseInt(termSplit[0]);
        int month = Integer.parseInt(termSplit[1]);
        LocalDate term = LocalDate.of(year, month, 1);
        int lastDay = YearMonth.from(term).lengthOfMonth();
        Map<String, AdminBestSalesProductDTO> bestMap = new HashMap<>();

        for(ProductSalesSummary summary : productSalesSummaryList) {
            if(summary.getPeriodMonth().equals(term)) {
                String productName = summary.getProduct().getProductName();
                AdminBestSalesProductDTO mapData = bestMap.getOrDefault(productName, new AdminBestSalesProductDTO(productName));

                bestMap.put(
                        productName,
                        new AdminBestSalesProductDTO(
                                productName,
                                mapData.productPeriodSalesQuantity() + summary.getSalesQuantity(),
                                mapData.productPeriodSales() + summary.getSales()
                        )
                );
            }
        }

        List<AdminBestSalesProductDTO> best5List = bestMap.values()
                .stream()
                .sorted(Comparator.comparingLong(AdminBestSalesProductDTO::productPeriodSalesQuantity).reversed())
                .limit(5)
                .toList();

        long monthSales = 0L;
        long monthSalesQuantity = 0L;
        long monthOrderQuantity = 0L;
        long lastYearMonthSales = 0L;
        long lastYearMonthSalesQuantity = 0L;
        long lastYearMonthOrderQuantity = 0L;
        Map<Integer, AdminPeriodSalesListDTO> dailyMap = new HashMap<>();
        for(PeriodSalesSummary summary : periodSalesSummaryList) {
            if(summary.getPeriod().getYear() == year && summary.getPeriod().getMonthValue() == month) {
                monthSales += summary.getSales();
                monthSalesQuantity += summary.getSalesQuantity();
                monthOrderQuantity += summary.getOrderQuantity();

                int day = summary.getPeriod().getDayOfMonth();
                AdminPeriodSalesListDTO mapDTO = dailyMap.getOrDefault(day, new AdminPeriodSalesListDTO(day));

                AdminPeriodSalesListDTO putDTO = new AdminPeriodSalesListDTO(
                        day,
                        mapDTO.sales() + summary.getSales(),
                        mapDTO.salesQuantity() + summary.getSalesQuantity(),
                        mapDTO.orderQuantity() + summary.getOrderQuantity()
                );

                dailyMap.put(day, putDTO);
            }
        }

        Map<String, AdminPeriodClassificationDTO> classificationMap = new HashMap<>();
        for(ProductSalesSummary summary : productSalesSummaryList) {
            if(summary.getPeriodMonth().getYear() == year && summary.getPeriodMonth().getMonthValue() == month) {
                String classificationId = summary.getClassification().getId();
                AdminPeriodClassificationDTO mapDTO = classificationMap.getOrDefault(classificationId, new AdminPeriodClassificationDTO(classificationId));

                AdminPeriodClassificationDTO putDTO = new AdminPeriodClassificationDTO(
                        classificationId,
                        mapDTO.classificationSales() + summary.getSales(),
                        mapDTO.classificationSalesQuantity() + summary.getSalesQuantity()
                );

                classificationMap.put(classificationId, putDTO);
            }
        }

        for(PeriodSalesSummary summary : periodSalesSummaryList) {
            if(summary.getPeriod().getYear() == year - 1 && summary.getPeriod().getMonthValue() == month) {
                lastYearMonthSales += summary.getSales();
                lastYearMonthSalesQuantity += summary.getSalesQuantity();
                lastYearMonthOrderQuantity += summary.getOrderQuantity();
            }
        }

        AdminPeriodMonthDetailResponseDTO result = Assertions.assertDoesNotThrow(() -> adminSalesService.getPeriodSalesDetail(TERM_MONTH));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(monthSales, result.monthSales());
        Assertions.assertEquals(monthSalesQuantity, result.monthSalesQuantity());
        Assertions.assertEquals(monthOrderQuantity, result.monthOrderQuantity());
        Assertions.assertEquals(monthSales - lastYearMonthSales, result.lastYearComparison());
        Assertions.assertEquals(lastYearMonthSales, result.lastYearSales());
        Assertions.assertEquals(lastYearMonthSalesQuantity, result.lastYearSalesQuantity());
        Assertions.assertEquals(lastYearMonthOrderQuantity, result.lastYearOrderQuantity());
        Assertions.assertFalse(result.bestProduct().isEmpty());
        Assertions.assertEquals(classificationList.size(), result.bestProduct().size());
        Assertions.assertFalse(result.classificationSales().isEmpty());
        Assertions.assertEquals(classificationList.size(), result.classificationSales().size());
        Assertions.assertFalse(result.dailySales().isEmpty());
        Assertions.assertEquals(lastDay, result.dailySales().size());

        for(int i = 1; i <= lastDay; i++) {
            AdminPeriodSalesListDTO daily = dailyMap.getOrDefault(i, new AdminPeriodSalesListDTO(i));
            AdminPeriodSalesListDTO resultDaily = result.dailySales().get(i - 1);

            Assertions.assertEquals(daily, resultDaily);
        }

        for(int i = 0; i < result.classificationSales().size(); i++) {
            AdminPeriodClassificationDTO resultDTO = result.classificationSales().get(i);
            AdminPeriodClassificationDTO classification = classificationMap.getOrDefault(resultDTO.classification(), new AdminPeriodClassificationDTO(resultDTO.classification()));

            Assertions.assertEquals(classification.classificationSales(), resultDTO.classificationSales());
            Assertions.assertEquals(classification.classificationSalesQuantity(), resultDTO.classificationSalesQuantity());
        }
    }

    @Test
    @DisplayName(value = "기간별 매출 상세 조회. 데이터가 없는 경우")
    void getPeriodSalesDetailEmpty() {
        String emptyTerm = "1900-01";
        String[] termSplit = emptyTerm.split("-");
        int year = Integer.parseInt(termSplit[0]);
        int month = Integer.parseInt(termSplit[1]);
        LocalDate term = LocalDate.of(year, month, 1);
        int lastDay = YearMonth.from(term).lengthOfMonth();

        AdminPeriodMonthDetailResponseDTO result = Assertions.assertDoesNotThrow(() -> adminSalesService.getPeriodSalesDetail(emptyTerm));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.monthSales());
        Assertions.assertEquals(0, result.monthSalesQuantity());
        Assertions.assertEquals(0, result.monthOrderQuantity());
        Assertions.assertEquals(0, result.lastYearComparison());
        Assertions.assertEquals(0, result.lastYearSales());
        Assertions.assertEquals(0, result.lastYearSalesQuantity());
        Assertions.assertEquals(0, result.lastYearOrderQuantity());
        Assertions.assertTrue(result.bestProduct().isEmpty());
        Assertions.assertFalse(result.classificationSales().isEmpty());
        Assertions.assertEquals(classificationList.size(), result.classificationSales().size());
        Assertions.assertFalse(result.dailySales().isEmpty());
        Assertions.assertEquals(lastDay, result.dailySales().size());

        for(AdminPeriodClassificationDTO classificationDTO : result.classificationSales()) {
            Assertions.assertEquals(0, classificationDTO.classificationSales());
            Assertions.assertEquals(0, classificationDTO.classificationSalesQuantity());
        }

        for(AdminPeriodSalesListDTO daily : result.dailySales()) {
            Assertions.assertEquals(0, daily.sales());
            Assertions.assertEquals(0, daily.salesQuantity());
            Assertions.assertEquals(0, daily.orderQuantity());
        }
    }

    @Test
    @DisplayName(value = "선택 상품 분류의 월 매출 내역 조회")
    void getSalesByClassification() {
        String[] termSplit = TERM_MONTH.split("-");
        int year = Integer.parseInt(termSplit[0]);
        int month = Integer.parseInt(termSplit[1]);
        String classification = classificationList.get(0).getId();
        long totalSales = 0L;
        long totalSalesQuantity = 0L;
        List<AdminClassificationSalesProductListDTO> productListDTO = new ArrayList<>();
        for(ProductSalesSummary summary : productSalesSummaryList) {
            if(summary.getPeriodMonth().getYear() == year
                    && summary.getPeriodMonth().getMonthValue() == month
                    && summary.getClassification().getId().equals(classification)) {
                totalSales += summary.getSales();
                totalSalesQuantity += summary.getSalesQuantity();

                productListDTO.add(
                        new AdminClassificationSalesProductListDTO(
                                summary.getProduct().getProductName(),
                                summary.getProductOption().getSize(),
                                summary.getProductOption().getColor(),
                                summary.getSales(),
                                summary.getSalesQuantity()
                        )
                );
            }
        }

        AdminClassificationSalesResponseDTO result = Assertions.assertDoesNotThrow(() -> adminSalesService.getSalesByClassification(TERM_MONTH, classification));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(classification, result.classification());
        Assertions.assertEquals(totalSales, result.totalSales());
        Assertions.assertEquals(totalSalesQuantity, result.totalSalesQuantity());
        Assertions.assertFalse(result.productList().isEmpty());
        Assertions.assertEquals(productListDTO.size(), result.productList().size());
    }

    @Test
    @DisplayName(value = "선택 상품 분류의 월 매출 내역 조회. 데이터가 없는 경우")
    void getSalesByClassificationEmpty() {
        productSalesSummaryRepository.deleteAll();
        String classification = classificationList.get(0).getId();
        int productSize = productOptionList.stream()
                .filter(v -> v.getProduct().getClassification().getId().equals(classification))
                .toList()
                .size();

        AdminClassificationSalesResponseDTO result = Assertions.assertDoesNotThrow(() -> adminSalesService.getSalesByClassification(TERM_MONTH, classification));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(classification, result.classification());
        Assertions.assertEquals(0, result.totalSales());
        Assertions.assertEquals(0, result.totalSalesQuantity());
        Assertions.assertFalse(result.productList().isEmpty());
        Assertions.assertEquals(productSize, result.productList().size());

        for(AdminClassificationSalesProductListDTO resultDTO : result.productList()) {
            Assertions.assertEquals(0, resultDTO.productSales());
            Assertions.assertEquals(0, resultDTO.productSalesQuantity());
        }
    }

    @Test
    @DisplayName(value = "일 매출 조회")
    void getSalesByDay() {
        int[] termSplit = Arrays.stream(TERM.split("-"))
                .mapToInt(Integer::parseInt)
                .toArray();
        int year = termSplit[0];
        int month = termSplit[1];
        int day = termSplit[2];
        long totalSales = 0L;
        long totalSalesQuantity = 0L;
        long totalOrderQuantity = 0L;
        Map<String, AdminPeriodClassificationDTO> classificationMap = new HashMap<>();

        for(ProductSalesSummary summary : productSalesSummaryList) {
            if(summary.getPeriodMonth().getYear() == year
                && summary.getPeriodMonth().getMonthValue() == month
                && summary.getPeriodMonth().getDayOfMonth() == day) {
                String classification = summary.getClassification().getId();
                AdminPeriodClassificationDTO mapData = classificationMap.getOrDefault(classification, new AdminPeriodClassificationDTO(classification));

                classificationMap.put(
                        classification,
                        new AdminPeriodClassificationDTO(
                                classification,
                                mapData.classificationSales() + summary.getSales(),
                                mapData.classificationSalesQuantity() + summary.getSalesQuantity()
                        )
                );
            }
        }

        for(PeriodSalesSummary summary : periodSalesSummaryList) {
            if(summary.getPeriod().getYear() == year
                    && summary.getPeriod().getMonthValue() == month
                    && summary.getPeriod().getDayOfMonth() == day) {
                totalSales = summary.getSales();
                totalSalesQuantity = summary.getSalesQuantity();
                totalOrderQuantity = summary.getOrderQuantity();

                break;
            }
        }

        AdminPeriodSalesResponseDTO<AdminPeriodClassificationDTO> result = Assertions.assertDoesNotThrow(() -> adminSalesService.getSalesByDay(TERM));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertEquals(classificationList.size(), result.content().size());
        Assertions.assertEquals(totalSales, result.sales());
        Assertions.assertEquals(totalSalesQuantity, result.salesQuantity());
        Assertions.assertEquals(totalOrderQuantity, result.orderQuantity());

        for(AdminPeriodClassificationDTO classificationDTO : result.content()) {
            String resultClassification = classificationDTO.classification();
            AdminPeriodClassificationDTO fixture = classificationMap.getOrDefault(resultClassification, new AdminPeriodClassificationDTO(resultClassification));

            Assertions.assertEquals(fixture, classificationDTO);
        }
    }

    @Test
    @DisplayName(value = "일 매출 조회. 데이터가 없는 경우")
    void getSalesByDayEmpty() {
        AdminPeriodSalesResponseDTO<AdminPeriodClassificationDTO> result = Assertions.assertDoesNotThrow(() -> adminSalesService.getSalesByDay("1900-01-01"));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.content().isEmpty());
        Assertions.assertEquals(0, result.sales());
        Assertions.assertEquals(0, result.salesQuantity());
        Assertions.assertEquals(0, result.orderQuantity());
    }

    @Test
    @DisplayName(value = "선택 일자의 주문 내역 조회")
    void getOrderListByDay() {
        LocalDate orderTerm = LocalDate.now().minusDays(1);
        int year = orderTerm.getYear();
        int month = orderTerm.getMonthValue();
        int day = orderTerm.getDayOfMonth();
        String term = orderTerm.toString();
        int page = 1;
        int amount = PageAmount.ADMIN_DAILY_ORDER_AMOUNT.getAmount();
        List<ProductOrder> orderFixtureList = orderList.stream()
                                                .filter(v -> v.getCreatedAt().getYear() == year
                                                        && v.getCreatedAt().getMonthValue() == month
                                                        && v.getCreatedAt().getDayOfMonth() == day)
                                                .toList();
        int totalPages = PaginationUtils.getTotalPages(orderFixtureList.size(), amount);
        int contentSize = Math.min(orderFixtureList.size(), amount);

        PagingListDTO<AdminDailySalesResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminSalesService.getOrderListByDay(term, page));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertEquals(contentSize, result.content().size());
        Assertions.assertEquals(orderFixtureList.size(), result.pagingData().getTotalElements());
        Assertions.assertEquals(totalPages, result.pagingData().getTotalPages());
        Assertions.assertFalse(result.pagingData().isEmpty());

        result.content().forEach(v -> Assertions.assertFalse(v.detailList().isEmpty()));
    }

    @Test
    @DisplayName(value = "선택 일자의 주문 내역 조회. 데이터가 없는 경우")
    void getOrderListByDayEmpty() {
        int page = 1;
        PagingListDTO<AdminDailySalesResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminSalesService.getOrderListByDay("1900-01-01", page));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.content().isEmpty());
        Assertions.assertEquals(0, result.pagingData().getTotalElements());
        Assertions.assertEquals(0, result.pagingData().getTotalPages());
        Assertions.assertTrue(result.pagingData().isEmpty());
    }

    @Test
    @DisplayName(value = "상품별 매출 조회")
    void getProductSalesList() {
        AdminPageDTO pageDTO = AdminPageDTOFixture.createDefaultAdminPageDTO();
        int totalPages = PaginationUtils.getTotalPages(productList.size(), pageDTO.amount());
        int contentSize = Math.min(productList.size(), pageDTO.amount());
        Page<AdminProductSalesListDTO> result = Assertions.assertDoesNotThrow(() -> adminSalesService.getProductSalesList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.getContent().isEmpty());
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(productList.size(), result.getTotalElements());
        Assertions.assertEquals(totalPages, result.getTotalPages());
        Assertions.assertEquals(contentSize, result.getContent().size());
    }

    @Test
    @DisplayName(value = "상품별 매출 조회. 상품 데이터는 있으나, 매출 데이터가 없는 경우")
    void getProductSalesListSalesEmpty() {
        productSalesSummaryRepository.deleteAll();
        AdminPageDTO pageDTO = AdminPageDTOFixture.createDefaultAdminPageDTO();
        int totalPages = PaginationUtils.getTotalPages(productList.size(), pageDTO.amount());
        int contentSize = Math.min(productList.size(), pageDTO.amount());
        Page<AdminProductSalesListDTO> result = Assertions.assertDoesNotThrow(() -> adminSalesService.getProductSalesList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.getContent().isEmpty());
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(productList.size(), result.getTotalElements());
        Assertions.assertEquals(totalPages, result.getTotalPages());
        Assertions.assertEquals(contentSize, result.getContent().size());

        result.getContent().forEach(v -> Assertions.assertEquals(0, v.sales()));
    }

    @Test
    @DisplayName(value = "상품별 매출 조회. 상품 데이터와 매출 데이터가 모두 없는 경우")
    void getProductSalesListEmpty() {
        productOrderRepository.deleteAll();
        productSalesSummaryRepository.deleteAll();
        productOptionRepository.deleteAll();
        productRepository.deleteAll();
        AdminPageDTO pageDTO = AdminPageDTOFixture.createDefaultAdminPageDTO();

        Page<AdminProductSalesListDTO> result = Assertions.assertDoesNotThrow(() -> adminSalesService.getProductSalesList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getContent().isEmpty());
        Assertions.assertTrue(result.isEmpty());
        Assertions.assertEquals(0, result.getTotalElements());
        Assertions.assertEquals(0, result.getTotalPages());
    }

    @Test
    @DisplayName(value = "선택 상품 매출 조회")
    void getProductSalesDetail() {
        Product product = productList.get(0);
        int year = LocalDate.now().getYear();
        long totalSales = 0L;
        long totalSalesQuantity = product.getProductSalesQuantity();
        long sales = 0L;
        long salesQuantity = 0L;
        long lastYearSales = 0L;
        long lastYearSalesQuantity = 0L;
        Map<Long, AdminProductSalesOptionDTO> optionTotalMap = new HashMap<>();
        Map<Integer, AdminPeriodSalesListDTO> monthSalesMap = new HashMap<>();
        Map<Long, AdminProductSalesOptionDTO> optionThisYearMap = new HashMap<>();
        Map<Long, AdminProductSalesOptionDTO> optionLastYearMap = new HashMap<>();
        for(ProductSalesSummary summary : productSalesSummaryList) {
            if(summary.getProduct().getId().equals(product.getId())) {
                totalSales += summary.getSales();

                AdminProductSalesOptionDTO totalMap = optionTotalMap.getOrDefault(summary.getProductOption().getId(), new AdminProductSalesOptionDTO(summary.getProductOption()));
                optionTotalMap.put(
                        summary.getProductOption().getId(),
                        new AdminProductSalesOptionDTO(
                                summary.getProductOption(),
                                totalMap.optionSales() + summary.getSales(),
                                totalMap.optionSalesQuantity() + summary.getSalesQuantity()
                        )
                );


                if(summary.getPeriodMonth().getYear() == year) {
                    sales += summary.getSales();
                    salesQuantity += summary.getSalesQuantity();
                    int month = summary.getPeriodMonth().getMonthValue();
                    AdminPeriodSalesListDTO mapData = monthSalesMap.getOrDefault(month, new AdminPeriodSalesListDTO(month));

                    monthSalesMap.put(
                            summary.getPeriodMonth().getMonthValue(),
                            new AdminPeriodSalesListDTO(
                                    month,
                                    mapData.sales() + summary.getSales(),
                                    mapData.salesQuantity() + summary.getSalesQuantity(),
                                    mapData.orderQuantity() + summary.getOrderQuantity()
                            )
                    );

                    ProductOption option = summary.getProductOption();
                    AdminProductSalesOptionDTO yearMap = optionThisYearMap.getOrDefault(option.getId(), new AdminProductSalesOptionDTO(option));

                    optionThisYearMap.put(
                            option.getId(),
                            new AdminProductSalesOptionDTO(
                                    option,
                                    yearMap.optionSales() + summary.getSales(),
                                    yearMap.optionSalesQuantity() + summary.getSalesQuantity()
                            )
                    );
                }

                if(summary.getPeriodMonth().getYear() == (year - 1)){
                    lastYearSales += summary.getSales();
                    lastYearSalesQuantity += summary.getSalesQuantity();

                    ProductOption option = summary.getProductOption();
                    AdminProductSalesOptionDTO yearMap = optionLastYearMap.getOrDefault(option.getId(), new AdminProductSalesOptionDTO(option));

                    optionLastYearMap.put(
                            option.getId(),
                            new AdminProductSalesOptionDTO(
                                    option,
                                    yearMap.optionSales() + summary.getSales(),
                                    yearMap.optionSalesQuantity() + summary.getSalesQuantity()
                            )
                    );
                }
            }
        }

        AdminProductSalesDetailDTO result = Assertions.assertDoesNotThrow(() -> adminSalesService.getProductSalesDetail(product.getId()));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(product.getProductName(), result.productName());
        Assertions.assertEquals(totalSales, result.totalSales());
        Assertions.assertEquals(totalSalesQuantity, result.totalSalesQuantity());
        Assertions.assertEquals(sales, result.yearSales());
        Assertions.assertEquals(salesQuantity, result.yearSalesQuantity());
        Assertions.assertEquals((sales - lastYearSales), result.lastYearComparison());
        Assertions.assertEquals(lastYearSales, result.lastYearSales());
        Assertions.assertEquals(lastYearSalesQuantity, result.lastYearSalesQuantity());
        Assertions.assertFalse(result.monthSales().isEmpty());
        Assertions.assertEquals(12, result.monthSales().size());

        for(AdminPeriodSalesListDTO monthSales : result.monthSales()) {
            int month = monthSales.date();
            AdminPeriodSalesListDTO mapData = monthSalesMap.getOrDefault(
                    month,
                    new AdminPeriodSalesListDTO(month)
            );

            Assertions.assertEquals(mapData.sales(), monthSales.sales());
            Assertions.assertEquals(mapData.salesQuantity(), monthSales.salesQuantity());
            Assertions.assertEquals(mapData.orderQuantity(), monthSales.orderQuantity());
        }

        Assertions.assertFalse(result.optionTotalSales().isEmpty());
        Assertions.assertEquals(optionTotalMap.size(), result.optionTotalSales().size());

        for(AdminProductSalesOptionDTO optionSales : result.optionTotalSales()) {
            long optionId = optionSales.optionId();

            AdminProductSalesOptionDTO mapData = optionTotalMap.getOrDefault(
                    optionId,
                    new AdminProductSalesOptionDTO(optionId, optionSales.size(), optionSales.color())
            );

            Assertions.assertEquals(mapData.optionSales(), optionSales.optionSales());
            Assertions.assertEquals(mapData.optionSalesQuantity(), optionSales.optionSalesQuantity());
        }

        Assertions.assertFalse(result.optionYearSales().isEmpty());
        Assertions.assertEquals(optionThisYearMap.size(), result.optionYearSales().size());

        for(int i = 0; i < result.optionYearSales().size(); i++) {
            AdminProductSalesOptionDTO resultList = result.optionYearSales().get(i);
            AdminProductSalesOptionDTO fixture = optionThisYearMap.getOrDefault(resultList.optionId(), new AdminProductSalesOptionDTO(resultList.optionId(), resultList.size(), resultList.color()));

            Assertions.assertEquals(fixture.optionId(), resultList.optionId());
            Assertions.assertEquals(fixture.size(), resultList.size());
            Assertions.assertEquals(fixture.color(), resultList.color());
            Assertions.assertEquals(fixture.optionSales(), resultList.optionSales());
            Assertions.assertEquals(fixture.optionSalesQuantity(), resultList.optionSalesQuantity());
        }

        Assertions.assertFalse(result.optionLastYearSales().isEmpty());
        Assertions.assertEquals(optionLastYearMap.size(), result.optionLastYearSales().size());

        for(int i = 0; i < result.optionLastYearSales().size(); i++) {
            AdminProductSalesOptionDTO resultList = result.optionLastYearSales().get(i);
            AdminProductSalesOptionDTO fixture = optionLastYearMap.getOrDefault(resultList.optionId(), new AdminProductSalesOptionDTO(resultList.optionId(), resultList.size(), resultList.color()));

            Assertions.assertEquals(fixture.optionId(), resultList.optionId());
            Assertions.assertEquals(fixture.size(), resultList.size());
            Assertions.assertEquals(fixture.color(), resultList.color());
            Assertions.assertEquals(fixture.optionSales(), resultList.optionSales());
            Assertions.assertEquals(fixture.optionSalesQuantity(), resultList.optionSalesQuantity());
        }
    }

    @Test
    @DisplayName(value = "선택 상품 매출 조회. 상품 데이터가 없는 경우")
    void getProductSalesDetailNotFound() {
        Assertions.assertThrows(CustomNotFoundException.class,
                () -> adminSalesService.getProductSalesDetail("noneProductId")
        );
    }
}
