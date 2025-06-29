package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.Fixture.ClassificationFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.Fixture.SalesSummaryFixture;
import com.example.mansshop_boot.Fixture.domain.productSalesSummary.TestProductOptionSalesSummaryDTO;
import com.example.mansshop_boot.Fixture.domain.productSalesSummary.TestProductSalesSummaryDTO;
import com.example.mansshop_boot.Fixture.domain.productSalesSummary.TestSalesSummaryDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.admin.business.*;
import com.example.mansshop_boot.domain.dto.admin.out.AdminProductSalesListDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.entity.Classification;
import com.example.mansshop_boot.domain.entity.Product;
import com.example.mansshop_boot.domain.entity.ProductOption;
import com.example.mansshop_boot.domain.entity.ProductSalesSummary;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.productSales.ProductSalesSummaryRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MansShopBootApplication.class)
@ActiveProfiles("test")
@Transactional
public class ProductSalesSummaryRepositoryTest {

    @Autowired
    private ClassificationRepository classificationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private ProductSalesSummaryRepository productSalesSummaryRepository;

    private static final int PRODUCT_SIZE = 30;

    private List<ProductSalesSummary> salesSummaryList;

    private List<Classification> classifications;

    private List<ProductOption> productOptionList;

    private List<ProductOption> topProductOptionList;

    /**
     * ProductSalesSummary 날짜 범위는 2023-01-01 ~ 2024-12-01
     */

    private static final LocalDate START_DATE = LocalDate.of(2024, 1, 1);
    private static final LocalDate END_DATE = START_DATE.plusMonths(1);

    @BeforeEach
    void init() {
        List<Classification> classificationFixtureList = ClassificationFixture.createClassification();
        List<Product> productFixtureList = ProductFixture.createDefaultProductByOUTER(PRODUCT_SIZE);
        List<ProductOption> productOptionFixtureList = productFixtureList.stream()
                                                                        .flatMap(v -> v.getProductOptions().stream())
                                                                        .toList();
        classificationRepository.saveAll(classificationFixtureList);
        productRepository.saveAll(productFixtureList);
        productOptionRepository.saveAll(productOptionFixtureList);

        List<ProductSalesSummary> productSalesSummaryFixtureList = SalesSummaryFixture.createProductSalesSummary(productFixtureList);
        productSalesSummaryRepository.saveAll(productSalesSummaryFixtureList);
        salesSummaryList = productSalesSummaryFixtureList;
        classifications = classificationFixtureList;
        productOptionList = productOptionFixtureList;

        List<Product> topProductFixtureList = ProductFixture.createProductByClassificationName(productFixtureList.size() + 1, 10, "TOP");
        List<ProductOption> topOptionFixtureList = topProductFixtureList.stream()
                                                                        .flatMap(v -> v.getProductOptions().stream())
                                                                        .toList();
        productRepository.saveAll(topProductFixtureList);
        productOptionRepository.saveAll(topOptionFixtureList);

        topProductOptionList = topOptionFixtureList;
    }

    @Test
    @DisplayName(value = "해당 기간의 베스트 5 상품 조회")
    void findPeriodBestProductOrder() {
        List<AdminBestSalesProductDTO> dataList = SalesSummaryFixture.createBest5SalesDTO(salesSummaryList, START_DATE);
        List<AdminBestSalesProductDTO> result = productSalesSummaryRepository.findPeriodBestProductOrder(START_DATE, END_DATE);

        assertNotNull(result);
        assertEquals(5, result.size());
    }

    @Test
    @DisplayName(value = "해당 기간의 상품 분류별 매출 조회")
    void findPeriodClassification() {
        Map<String, TestSalesSummaryDTO> dataMap = SalesSummaryFixture.createSalesDataListByClassifications(salesSummaryList, START_DATE);
        List<AdminPeriodClassificationDTO> result = productSalesSummaryRepository.findPeriodClassification(START_DATE, END_DATE);

        assertNotNull(result);
        assertEquals(classifications.size(), result.size());

        for(AdminPeriodClassificationDTO resultData : result) {

            TestSalesSummaryDTO data = dataMap.getOrDefault(resultData.classification(), null);

            if(data == null) {
                assertEquals(0L, resultData.classificationSales());
                assertEquals(0L, resultData.classificationSalesQuantity());
            }else {
                assertEquals(data.getSales(), resultData.classificationSales());
                assertEquals(data.getSalesQuantity(), resultData.classificationSalesQuantity());
            }
        }
    }

    @Test
    @DisplayName(value = "해당 상품 분류의 기간 매출 조회")
    void findPeriodClassificationSales() {
        String classification = "OUTER";
        Map<String, TestSalesSummaryDTO> dataMap = SalesSummaryFixture.createSalesDataListByClassifications(salesSummaryList, START_DATE);
        AdminClassificationSalesDTO result = productSalesSummaryRepository.findPeriodClassificationSales(START_DATE, END_DATE, classification);

        TestSalesSummaryDTO outerSales = dataMap.get(classification);

        assertNotNull(result);
        assertEquals(outerSales.getSales(), result.sales());
        assertEquals(outerSales.getSalesQuantity(), result.salesQuantity());
        assertEquals(outerSales.getOrderQuantity(), result.orderQuantity());
    }

    @Test
    @DisplayName(value = "상품 분류에 해당하는 상품들의 기간 매출 조회")
    void findPeriodClassificationProductSales() {
        String classificationId = "OUTER";
        Map<String, TestSalesSummaryDTO> dataMap = SalesSummaryFixture.createSalesDataListByClassificationAndProduct(salesSummaryList, START_DATE, END_DATE, classificationId);
        List<AdminClassificationSalesProductListDTO> result = productSalesSummaryRepository.findPeriodClassificationProductSales(START_DATE, END_DATE, classificationId);

        result.forEach(System.out::println);

        assertNotNull(result);

        for(AdminClassificationSalesProductListDTO resultData : result) {
            String resultKey = resultData.productName() + "_" +
                                resultData.size() + "_" +
                                resultData.color();

            TestSalesSummaryDTO dataValue = dataMap.get(resultKey);

            assertEquals(dataValue.getSales(), resultData.productSales());
            assertEquals(dataValue.getSalesQuantity(), resultData.productSalesQuantity());
        }
    }

    @Test
    @DisplayName(value = "상품 분류에 해당하는 상품들의 기간 매출 조회. 데이터가 없는 경우")
    void findPeriodClassificationProductSalesEmpty() {
        String classificationId = "TOP";
        List<AdminClassificationSalesProductListDTO> result = productSalesSummaryRepository.findPeriodClassificationProductSales(START_DATE, END_DATE, classificationId);

        assertNotNull(result);
        assertEquals(topProductOptionList.size(), result.size());

        for(AdminClassificationSalesProductListDTO resultData : result) {
            assertEquals(0, resultData.productSales());
            assertEquals(0, resultData.productSalesQuantity());
        }
    }

    @Test
    @DisplayName(value = "상품별 매출 조회. 기간 상관없이 전체 매출을 조회")
    void findProductSalesList() {
        Map<String, TestProductSalesSummaryDTO> dataMap = SalesSummaryFixture.createSalesAllProductSalesSummary(salesSummaryList);
        AdminPageDTO pageDTO = new AdminPageDTO(null, 1);
        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                                            , pageDTO.amount()
                                            , Sort.by("classificationStep").ascending());
        Page<AdminProductSalesListDTO> result = productSalesSummaryRepository.findProductSalesList(pageDTO, pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        for(AdminProductSalesListDTO resultData : result.getContent()) {
            TestProductSalesSummaryDTO dataValue = dataMap.get(resultData.productId());

            assertEquals(dataValue.getClassificationId(), resultData.classification());
            assertEquals(dataValue.getProductName(), resultData.productName());
            assertEquals(dataValue.getSales(), resultData.sales());
            assertEquals(dataValue.getSalesQuantity(), resultData.salesQuantity());
        }
    }

    @Test
    @DisplayName(value = "상품별 매출 조회. 기간 상관없이 전체 매출을 조회. 특정 상품명을 검색")
    void findProductSalesListSearchProductName() {
        Map<String, TestProductSalesSummaryDTO> dataMap = SalesSummaryFixture.createSalesAllProductSalesSummary(salesSummaryList);
        String productName = salesSummaryList.get(0).getProduct().getProductName();
        AdminPageDTO pageDTO = new AdminPageDTO(productName, 1);
        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                , pageDTO.amount()
                , Sort.by("classificationStep").ascending());
        Page<AdminProductSalesListDTO> result = productSalesSummaryRepository.findProductSalesList(pageDTO, pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        for(AdminProductSalesListDTO resultData : result.getContent()) {
            TestProductSalesSummaryDTO dataValue = dataMap.get(resultData.productId());

            assertEquals(dataValue.getClassificationId(), resultData.classification());
            assertEquals(dataValue.getProductName(), resultData.productName());
            assertEquals(dataValue.getSales(), resultData.sales());
            assertEquals(dataValue.getSalesQuantity(), resultData.salesQuantity());
        }
    }

    @Test
    @DisplayName(value = "상품별 매출 조회. 기간 상관없이 전체 매출을 조회. 상품명 검색. 데이터가 없는 경우")
    void findProductSalesListSearchProductNameEmpty() {
        AdminPageDTO pageDTO = new AdminPageDTO("fakeProduct", 1);
        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                , pageDTO.amount()
                , Sort.by("classificationStep").ascending());
        Page<AdminProductSalesListDTO> result = productSalesSummaryRepository.findProductSalesList(pageDTO, pageable);

        System.out.println("result : " + result.getContent());

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName(value = "상품 아이디 기반 매출 조회. 기간 상관없이 전체 매출을 조회")
    void getProductSales() {
        String productId = salesSummaryList.get(0).getProduct().getId();
        String productName = "";
        long totalSales = 0L;
        long totalSalesQuantity = salesSummaryList.get(0).getProduct().getProductSalesQuantity();

        for(ProductSalesSummary data : salesSummaryList) {
            if(data.getProduct().getId().equals(productId)){
                totalSales += data.getSales();
                productName = data.getProduct().getProductName();
            }
        }

        AdminProductSalesDTO result = productSalesSummaryRepository.getProductSales(productId);

        assertNotNull(result);
        assertEquals(productName, result.productName());
        assertEquals(totalSales, result.totalSales());
        assertEquals(totalSalesQuantity, result.totalSalesQuantity());
    }

    @Test
    @DisplayName(value = "상품 아이디 기반 매출 조회. 기간 상관없이 전체 매출을 조회. 존재하지 않는 데이터인 경우")
    void getProductSalesEmpty() {
        AdminProductSalesDTO result = productSalesSummaryRepository.getProductSales("fakeProduct");

        assertNotNull(result);
        assertNull(result.productName());
        assertEquals(0, result.totalSales());
        assertEquals(0, result.totalSalesQuantity());
    }

    @Test
    @DisplayName(value = "상품 아이디 기반 연 매출 조회")
    void getProductPeriodSales() {
        int year = 2024;
        String productId = salesSummaryList.get(0).getProduct().getId();
        long sales = 0L;
        long salesQuantity = 0L;

        for(ProductSalesSummary data : salesSummaryList)
            if(data.getPeriodMonth().getYear() == year  && data.getProduct().getId().equals(productId)) {
                sales += data.getSales();
                salesQuantity += data.getSalesQuantity();
            }

        AdminSalesDTO result = productSalesSummaryRepository.getProductPeriodSales(year, productId);

        assertNotNull(result);
        assertEquals(sales, result.sales());
        assertEquals(salesQuantity, result.salesQuantity());
    }

    @Test
    @DisplayName(value = "상품 아이디 기반 연 매출 조회. 존재하지 않는 데이터인 경우")
    void getProductPeriodSalesEmpty() {
        int year = 2024;

        AdminSalesDTO result = productSalesSummaryRepository.getProductPeriodSales(year, "fakeProduct");

        assertEquals(0, result.sales());
        assertEquals(0, result.salesQuantity());
    }

    @Test
    @DisplayName(value = "상품 아이디 기반 해당 연도의 월별 매출 조회")
    void getProductMonthPeriodSales() {
        int year = 2024;
        String productId = salesSummaryList.get(0).getProduct().getId();
        Map<Integer, TestSalesSummaryDTO> dataMap = SalesSummaryFixture.createProductPeriodMonthSalesSummary(salesSummaryList, year, productId);
        List<AdminPeriodSalesListDTO> result = productSalesSummaryRepository.getProductMonthPeriodSales(year, productId);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        for(AdminPeriodSalesListDTO resultData : result) {
            TestSalesSummaryDTO data = dataMap.get(resultData.date());

            assertEquals(data.getSales(), resultData.sales());
            assertEquals(data.getSalesQuantity(), resultData.salesQuantity());
            assertEquals(data.getOrderQuantity(), resultData.orderQuantity());
        }
    }

    @Test
    @DisplayName(value = "상품 아이디 기반 해당 연도의 월별 매출 조회. 데이터가 없는 경우")
    void getProductMonthPeriodSalesEmpty() {
        int year = 2024;
        List<AdminPeriodSalesListDTO> result = productSalesSummaryRepository.getProductMonthPeriodSales(year, "fakeProduct");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName(value = "상품 아이디 기반 해당 상품의 옵션별 연매출 조회")
    void getProductOptionSales() {
        int year = 2024;
        String productId = salesSummaryList.get(0).getProduct().getId();
        Map<Long, TestProductOptionSalesSummaryDTO> dataMap = SalesSummaryFixture.createProductOptionPeriodYearSalesSummary(salesSummaryList, year, productId);
        List<AdminProductSalesOptionDTO> result = productSalesSummaryRepository.getProductOptionSales(year, productId);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        for(AdminProductSalesOptionDTO resultData : result) {
            TestProductOptionSalesSummaryDTO data = dataMap.get(resultData.optionId());

            assertEquals(data.getSize(), resultData.size());
            assertEquals(data.getColor(), resultData.color());
            assertEquals(data.getSales(), resultData.optionSales());
            assertEquals(data.getSalesQuantity(), resultData.optionSalesQuantity());
        }
    }

    @Test
    @DisplayName(value = "상품 아이디 기반 해당 상품의 옵션별 연매출 조회. 데이터가 없는 경우")
    void getProductOptionSalesEmpty() {
        int year = 2024;
        List<AdminProductSalesOptionDTO> result = productSalesSummaryRepository.getProductOptionSales(year, "fakeProduct");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName(value = "특정 날짜의 optionIds 조건에 만족하는 데이터 조회")
    void findAllByProductOptionIds() {
        LocalDate periodMonth = LocalDate.of(2024, 1, 1);
        List<Long> optionIds = productOptionList.stream()
                                                .map(ProductOption::getId)
                                                .limit(5)
                                                .toList();
        List<ProductSalesSummary> dataList = salesSummaryList.stream()
                                                .filter(v ->
                                                        v.getPeriodMonth().equals(periodMonth) && optionIds.contains(v.getProductOption().getId())
                                                )
                                                .toList();
        List<ProductSalesSummary> result = productSalesSummaryRepository.findAllByProductOptionIds(periodMonth, optionIds);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(optionIds.size(), result.size());
        assertEquals(dataList.size(), result.size());

        for(ProductSalesSummary resultData : result) {
            boolean flag = false;
            for(ProductSalesSummary data : dataList) {
                if(resultData.getId().equals(data.getId())){
                    flag = true;
                }
            }

            assertTrue(flag);
        }
    }
}
