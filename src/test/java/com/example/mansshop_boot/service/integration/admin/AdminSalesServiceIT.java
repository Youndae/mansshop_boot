package com.example.mansshop_boot.service.integration.admin;

import com.example.mansshop_boot.Fixture.*;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.util.PaginationUtils;
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
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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

        AdminPeriodSalesResponseDTO<AdminPeriodSalesListDTO> result = assertDoesNotThrow(() -> adminSalesService.getPeriodSales(TERM_YEAR));

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals(yearSales, result.sales());
        assertEquals(yearSalesQuantity, result.salesQuantity());
        assertEquals(yearOrderSalesQuantity, result.orderQuantity());

        for(AdminPeriodSalesListDTO content : result.content()) {
            AdminPeriodSalesListDTO mapData = fixtureMap.get(content.date());

            assertEquals(mapData.sales(), content.sales());
            assertEquals(mapData.salesQuantity(), content.salesQuantity());
            assertEquals(mapData.orderQuantity(), content.orderQuantity());
        }
    }

    @Test
    @DisplayName(value = "기간별 매출 조회. 데이터가 없는 경우")
    void getPeriodSalesEmpty() {
        AdminPeriodSalesResponseDTO<AdminPeriodSalesListDTO> result = assertDoesNotThrow(() -> adminSalesService.getPeriodSales(2000));

        assertNotNull(result);
        assertFalse(result.content().isEmpty());

        for(AdminPeriodSalesListDTO content : result.content()) {
            assertEquals(0, content.sales());
            assertEquals(0, content.salesQuantity());
            assertEquals(0, content.orderQuantity());
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

        AdminPeriodMonthDetailResponseDTO result = assertDoesNotThrow(() -> adminSalesService.getPeriodSalesDetail(TERM_MONTH));

        assertNotNull(result);
        assertEquals(monthSales, result.monthSales());
        assertEquals(monthSalesQuantity, result.monthSalesQuantity());
        assertEquals(monthOrderQuantity, result.monthOrderQuantity());
        assertEquals(monthSales - lastYearMonthSales, result.lastYearComparison());
        assertEquals(lastYearMonthSales, result.lastYearSales());
        assertEquals(lastYearMonthSalesQuantity, result.lastYearSalesQuantity());
        assertEquals(lastYearMonthOrderQuantity, result.lastYearOrderQuantity());
        assertFalse(result.bestProduct().isEmpty());
        assertEquals(classificationList.size(), result.bestProduct().size());
        assertFalse(result.classificationSales().isEmpty());
        assertEquals(classificationList.size(), result.classificationSales().size());
        assertFalse(result.dailySales().isEmpty());
        assertEquals(lastDay, result.dailySales().size());

        for(int i = 1; i <= lastDay; i++) {
            AdminPeriodSalesListDTO daily = dailyMap.getOrDefault(i, new AdminPeriodSalesListDTO(i));
            AdminPeriodSalesListDTO resultDaily = result.dailySales().get(i - 1);

            assertEquals(daily, resultDaily);
        }

        for(int i = 0; i < result.classificationSales().size(); i++) {
            AdminPeriodClassificationDTO resultDTO = result.classificationSales().get(i);
            AdminPeriodClassificationDTO classification = classificationMap.getOrDefault(resultDTO.classification(), new AdminPeriodClassificationDTO(resultDTO.classification()));

            assertEquals(classification.classificationSales(), resultDTO.classificationSales());
            assertEquals(classification.classificationSalesQuantity(), resultDTO.classificationSalesQuantity());
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

        AdminPeriodMonthDetailResponseDTO result = assertDoesNotThrow(() -> adminSalesService.getPeriodSalesDetail(emptyTerm));

        assertNotNull(result);
        assertEquals(0, result.monthSales());
        assertEquals(0, result.monthSalesQuantity());
        assertEquals(0, result.monthOrderQuantity());
        assertEquals(0, result.lastYearComparison());
        assertEquals(0, result.lastYearSales());
        assertEquals(0, result.lastYearSalesQuantity());
        assertEquals(0, result.lastYearOrderQuantity());
        assertTrue(result.bestProduct().isEmpty());
        assertFalse(result.classificationSales().isEmpty());
        assertEquals(classificationList.size(), result.classificationSales().size());
        assertFalse(result.dailySales().isEmpty());
        assertEquals(lastDay, result.dailySales().size());

        for(AdminPeriodClassificationDTO classificationDTO : result.classificationSales()) {
            assertEquals(0, classificationDTO.classificationSales());
            assertEquals(0, classificationDTO.classificationSalesQuantity());
        }

        for(AdminPeriodSalesListDTO daily : result.dailySales()) {
            assertEquals(0, daily.sales());
            assertEquals(0, daily.salesQuantity());
            assertEquals(0, daily.orderQuantity());
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

        AdminClassificationSalesResponseDTO result = assertDoesNotThrow(() -> adminSalesService.getSalesByClassification(TERM_MONTH, classification));

        assertNotNull(result);
        assertEquals(classification, result.classification());
        assertEquals(totalSales, result.totalSales());
        assertEquals(totalSalesQuantity, result.totalSalesQuantity());
        assertFalse(result.productList().isEmpty());
        assertEquals(productListDTO.size(), result.productList().size());
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

        AdminClassificationSalesResponseDTO result = assertDoesNotThrow(() -> adminSalesService.getSalesByClassification(TERM_MONTH, classification));

        assertNotNull(result);
        assertEquals(classification, result.classification());
        assertEquals(0, result.totalSales());
        assertEquals(0, result.totalSalesQuantity());
        assertFalse(result.productList().isEmpty());
        assertEquals(productSize, result.productList().size());

        for(AdminClassificationSalesProductListDTO resultDTO : result.productList()) {
            assertEquals(0, resultDTO.productSales());
            assertEquals(0, resultDTO.productSalesQuantity());
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

        AdminPeriodSalesResponseDTO<AdminPeriodClassificationDTO> result = assertDoesNotThrow(() -> adminSalesService.getSalesByDay(TERM));

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals(classificationList.size(), result.content().size());
        assertEquals(totalSales, result.sales());
        assertEquals(totalSalesQuantity, result.salesQuantity());
        assertEquals(totalOrderQuantity, result.orderQuantity());

        for(AdminPeriodClassificationDTO classificationDTO : result.content()) {
            String resultClassification = classificationDTO.classification();
            AdminPeriodClassificationDTO fixture = classificationMap.getOrDefault(resultClassification, new AdminPeriodClassificationDTO(resultClassification));

            assertEquals(fixture, classificationDTO);
        }
    }

    @Test
    @DisplayName(value = "일 매출 조회. 데이터가 없는 경우")
    void getSalesByDayEmpty() {
        AdminPeriodSalesResponseDTO<AdminPeriodClassificationDTO> result = assertDoesNotThrow(() -> adminSalesService.getSalesByDay("1900-01-01"));

        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertEquals(0, result.sales());
        assertEquals(0, result.salesQuantity());
        assertEquals(0, result.orderQuantity());
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

        PagingListDTO<AdminDailySalesResponseDTO> result = assertDoesNotThrow(() -> adminSalesService.getOrderListByDay(term, page));

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals(contentSize, result.content().size());
        assertEquals(orderFixtureList.size(), result.pagingData().getTotalElements());
        assertEquals(totalPages, result.pagingData().getTotalPages());
        assertFalse(result.pagingData().isEmpty());

        result.content().forEach(v -> assertFalse(v.detailList().isEmpty()));
    }

    @Test
    @DisplayName(value = "선택 일자의 주문 내역 조회. 데이터가 없는 경우")
    void getOrderListByDayEmpty() {
        int page = 1;
        PagingListDTO<AdminDailySalesResponseDTO> result = assertDoesNotThrow(() -> adminSalesService.getOrderListByDay("1900-01-01", page));

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
        int totalPages = PaginationUtils.getTotalPages(productList.size(), pageDTO.amount());
        int contentSize = Math.min(productList.size(), pageDTO.amount());
        Page<AdminProductSalesListDTO> result = assertDoesNotThrow(() -> adminSalesService.getProductSalesList(pageDTO));

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertFalse(result.isEmpty());
        assertEquals(productList.size(), result.getTotalElements());
        assertEquals(totalPages, result.getTotalPages());
        assertEquals(contentSize, result.getContent().size());
    }

    @Test
    @DisplayName(value = "상품별 매출 조회. 상품 데이터는 있으나, 매출 데이터가 없는 경우")
    void getProductSalesListSalesEmpty() {
        productSalesSummaryRepository.deleteAll();
        AdminPageDTO pageDTO = AdminPageDTOFixture.createDefaultAdminPageDTO();
        int totalPages = PaginationUtils.getTotalPages(productList.size(), pageDTO.amount());
        int contentSize = Math.min(productList.size(), pageDTO.amount());
        Page<AdminProductSalesListDTO> result = assertDoesNotThrow(() -> adminSalesService.getProductSalesList(pageDTO));

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertFalse(result.isEmpty());
        assertEquals(productList.size(), result.getTotalElements());
        assertEquals(totalPages, result.getTotalPages());
        assertEquals(contentSize, result.getContent().size());

        result.getContent().forEach(v -> assertEquals(0, v.sales()));
    }

    @Test
    @DisplayName(value = "상품별 매출 조회. 상품 데이터와 매출 데이터가 모두 없는 경우")
    void getProductSalesListEmpty() {
        productOrderRepository.deleteAll();
        productSalesSummaryRepository.deleteAll();
        productOptionRepository.deleteAll();
        productRepository.deleteAll();
        AdminPageDTO pageDTO = AdminPageDTOFixture.createDefaultAdminPageDTO();

        Page<AdminProductSalesListDTO> result = assertDoesNotThrow(() -> adminSalesService.getProductSalesList(pageDTO));

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
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

        AdminProductSalesDetailDTO result = assertDoesNotThrow(() -> adminSalesService.getProductSalesDetail(product.getId()));

        assertNotNull(result);
        assertEquals(product.getProductName(), result.productName());
        assertEquals(totalSales, result.totalSales());
        assertEquals(totalSalesQuantity, result.totalSalesQuantity());
        assertEquals(sales, result.yearSales());
        assertEquals(salesQuantity, result.yearSalesQuantity());
        assertEquals((sales - lastYearSales), result.lastYearComparison());
        assertEquals(lastYearSales, result.lastYearSales());
        assertEquals(lastYearSalesQuantity, result.lastYearSalesQuantity());
        assertFalse(result.monthSales().isEmpty());
        assertEquals(12, result.monthSales().size());

        for(AdminPeriodSalesListDTO monthSales : result.monthSales()) {
            int month = monthSales.date();
            AdminPeriodSalesListDTO mapData = monthSalesMap.getOrDefault(
                    month,
                    new AdminPeriodSalesListDTO(month)
            );

            assertEquals(mapData.sales(), monthSales.sales());
            assertEquals(mapData.salesQuantity(), monthSales.salesQuantity());
            assertEquals(mapData.orderQuantity(), monthSales.orderQuantity());
        }

        assertFalse(result.optionTotalSales().isEmpty());
        assertEquals(optionTotalMap.size(), result.optionTotalSales().size());

        for(AdminProductSalesOptionDTO optionSales : result.optionTotalSales()) {
            long optionId = optionSales.optionId();

            AdminProductSalesOptionDTO mapData = optionTotalMap.getOrDefault(
                    optionId,
                    new AdminProductSalesOptionDTO(optionId, optionSales.size(), optionSales.color())
            );

            assertEquals(mapData.optionSales(), optionSales.optionSales());
            assertEquals(mapData.optionSalesQuantity(), optionSales.optionSalesQuantity());
        }

        assertFalse(result.optionYearSales().isEmpty());
        assertEquals(optionThisYearMap.size(), result.optionYearSales().size());

        for(int i = 0; i < result.optionYearSales().size(); i++) {
            AdminProductSalesOptionDTO resultList = result.optionYearSales().get(i);
            AdminProductSalesOptionDTO fixture = optionThisYearMap.getOrDefault(resultList.optionId(), new AdminProductSalesOptionDTO(resultList.optionId(), resultList.size(), resultList.color()));

            assertEquals(fixture.optionId(), resultList.optionId());
            assertEquals(fixture.size(), resultList.size());
            assertEquals(fixture.color(), resultList.color());
            assertEquals(fixture.optionSales(), resultList.optionSales());
            assertEquals(fixture.optionSalesQuantity(), resultList.optionSalesQuantity());
        }

        assertFalse(result.optionLastYearSales().isEmpty());
        assertEquals(optionLastYearMap.size(), result.optionLastYearSales().size());

        for(int i = 0; i < result.optionLastYearSales().size(); i++) {
            AdminProductSalesOptionDTO resultList = result.optionLastYearSales().get(i);
            AdminProductSalesOptionDTO fixture = optionLastYearMap.getOrDefault(resultList.optionId(), new AdminProductSalesOptionDTO(resultList.optionId(), resultList.size(), resultList.color()));

            assertEquals(fixture.optionId(), resultList.optionId());
            assertEquals(fixture.size(), resultList.size());
            assertEquals(fixture.color(), resultList.color());
            assertEquals(fixture.optionSales(), resultList.optionSales());
            assertEquals(fixture.optionSalesQuantity(), resultList.optionSalesQuantity());
        }
    }

    @Test
    @DisplayName(value = "선택 상품 매출 조회. 상품 데이터가 없는 경우")
    void getProductSalesDetailNotFound() {
        assertThrows(CustomNotFoundException.class,
                () -> adminSalesService.getProductSalesDetail("noneProductId")
        );
    }
}
