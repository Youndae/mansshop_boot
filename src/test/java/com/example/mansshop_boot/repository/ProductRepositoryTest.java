package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.Fixture.ClassificationFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.admin.business.AdminProductStockDataDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminDiscountPatchDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminDiscountProductDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminProductListDTO;
import com.example.mansshop_boot.domain.dto.main.business.MainListDTO;
import com.example.mansshop_boot.domain.dto.order.business.ProductIdClassificationDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.MainPageDTO;
import com.example.mansshop_boot.domain.entity.Classification;
import com.example.mansshop_boot.domain.entity.Product;
import com.example.mansshop_boot.domain.entity.ProductOption;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = MansShopBootApplication.class)
@ActiveProfiles("test")
@Transactional
public class ProductRepositoryTest {

    @Autowired
    private ClassificationRepository classificationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    private Product updateProduct;

    private final int PRODUCT_SIZE = 30;

    private List<String> productIds;

    private List<Product> productList;

    @BeforeAll
    void init() {
        List<Classification> classificationList = ClassificationFixture.createClassification();
        List<Product> productFixtureList = ProductFixture.createDefaultProductByOUTER(PRODUCT_SIZE);
        List<ProductOption> optionList = productFixtureList.stream().flatMap(v -> v.getProductOptions().stream()).toList();

        classificationRepository.saveAll(classificationList);
        productRepository.saveAll(productFixtureList);
        productOptionRepository.saveAll(optionList);

        updateProduct = productFixtureList.get(0);
        productList = productFixtureList;
        productIds = IntStream.range(0, 6)
                                .mapToObj(v -> productFixtureList.get(v).getId())
                                .toList();
    }

    @Test
    @DisplayName(value = "메인페이지 BEST 조회")
    void findListDefaultBEST() {
        MainPageDTO pageDTO = new MainPageDTO(1, null, "BEST");
        List<MainListDTO> result = productRepository.findListDefault(pageDTO);

        assertNotNull(result);
        assertEquals(pageDTO.mainProductAmount(), result.size());
    }

    @Test
    @DisplayName(value = "메인페이지 NEW 조회")
    void findListDefaultNEW() {
        MainPageDTO pageDTO = new MainPageDTO(1, null, "NEW");
        List<MainListDTO> result = productRepository.findListDefault(pageDTO);

        assertNotNull(result);
        assertEquals(pageDTO.mainProductAmount(), result.size());
    }

    @Test
    @DisplayName(value = "메인페이지 OUTER 조회")
    void findListDefaultOUTER() {
        MainPageDTO pageDTO = new MainPageDTO(1, null, "OUTER");
        Pageable pageable =  PageRequest.of(pageDTO.pageNum() - 1
                                                    , pageDTO.mainProductAmount()
                                                    , Sort.by("createdAt").descending()
                                            );
        Page<MainListDTO> result = productRepository.findListPageable(pageDTO, pageable);

        assertNotNull(result);
        assertEquals(PRODUCT_SIZE, result.getTotalElements());
        assertEquals(pageDTO.mainProductAmount(), result.getContent().size());
    }

    @Test
    @DisplayName(value = "메인페이지 TOP 조회. 데이터가 없어야 함.")
    void findListDefaultTOP() {
        MainPageDTO pageDTO = new MainPageDTO(1, null, "TOP");
        Pageable pageable =  PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.mainProductAmount()
                , Sort.by("createdAt").descending()
        );
        Page<MainListDTO> result = productRepository.findListPageable(pageDTO, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName(value = "상품 아이디 리스트 기반 상품 리스트 조회")
    void findAllByIdList() {
        List<Product> result = productRepository.findAllByIdList(productIds);

        assertNotNull(result);
        assertEquals(productIds.size(), result.size());
        for(int i = 0; i < productIds.size(); i++)
            assertEquals(productIds.get(i), result.get(i).getId());
    }

    @Test
    @DisplayName(value = "관리자 상품 리스트 조회")
    void findAdminProductList() {
        AdminPageDTO pageDTO = new AdminPageDTO(null, 1);
        List<AdminProductListDTO> result = productRepository.findAdminProductList(pageDTO);

        assertNotNull(result);
        assertEquals(pageDTO.amount(), result.size());
    }

    @Test
    @DisplayName(value = "관리자 상품 리스트 검색")
    void findAdminProductListSearch() {
        AdminPageDTO pageDTO = new AdminPageDTO(String.valueOf(PRODUCT_SIZE - 1), 1);
        List<AdminProductListDTO> result = productRepository.findAdminProductList(pageDTO);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName(value = "관리자 상품 리스트 검색. 없는 상품명 조회")
    void findAdminProductListSearchEmpty() {
        AdminPageDTO pageDTO = new AdminPageDTO("FAIL", 1);
        List<AdminProductListDTO> result = productRepository.findAdminProductList(pageDTO);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName(value = "관리자 상품 리스트 count")
    void findAdminProductListCount() {
        AdminPageDTO pageDTO = new AdminPageDTO(null, 1);
        Long result = productRepository.findAdminProductListCount(pageDTO);

        assertNotNull(result);
        assertEquals(PRODUCT_SIZE, result);
    }

    @Test
    @DisplayName(value = "관리자 상품 리스트 count 검색")
    void findAdminProductListCountSearch() {
        AdminPageDTO pageDTO = new AdminPageDTO(String.valueOf(PRODUCT_SIZE - 1), 1);
        Long result = productRepository.findAdminProductListCount(pageDTO);

        assertNotNull(result);
        assertEquals(1L, result);
    }

    @Test
    @DisplayName(value = "관리자 상품 리스트 count 검색. 없는 상품명인 경우")
    void findAdminProductListCountSearchEmpty() {
        AdminPageDTO pageDTO = new AdminPageDTO("FAIL", 1);
        Long result = productRepository.findAdminProductListCount(pageDTO);

        assertNotNull(result);
        assertEquals(0L, result);
    }

    @Test
    @DisplayName(value = "상품 재고 기반 조회")
    void findStockData() {
        AdminPageDTO pageDTO = new AdminPageDTO(null, 1);
        List<AdminProductStockDataDTO> result = productRepository.findStockData(pageDTO);

        assertNotNull(result);
        assertEquals(pageDTO.amount(), result.size());
    }

    @Test
    @DisplayName(value = "상품 재고 기반 검색")
    void findStockDataSearch() {
        Product product = productList.get(PRODUCT_SIZE - 1);
        AdminPageDTO pageDTO = new AdminPageDTO(product.getProductName(), 1);
        List<AdminProductStockDataDTO> result = productRepository.findStockData(pageDTO);
        AdminProductStockDataDTO data = new AdminProductStockDataDTO(product.getId(),
                product.getClassification().getId(),
                product.getProductName(),
                product.getProductOptions().stream().mapToInt(ProductOption::getStock).sum(),
                product.isOpen());

        assertNotNull(result);
        assertEquals(1, result.size());
        AdminProductStockDataDTO resultData = result.get(0);

        assertEquals(data.productId(), resultData.productId());
        assertEquals(data.totalStock(), resultData.totalStock());
    }

    @Test
    @DisplayName(value = "상품 재고 기반 검색. 없는 상품")
    void findStockDataSearchEmpty() {
        AdminPageDTO pageDTO = new AdminPageDTO("FAIL", 1);
        List<AdminProductStockDataDTO> result = productRepository.findStockData(pageDTO);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private List<AdminProductStockDataDTO> getProductListOrderByTotalStock() {
        List<AdminProductStockDataDTO> result = productList.stream()
                .map(v -> {
                    int totalStock = v.getProductOptions().stream().mapToInt(ProductOption::getStock).sum();

                    return new AdminProductStockDataDTO(v.getId(),
                                                        v.getClassification().getId(),
                                                        v.getProductName(),
                                                        totalStock,
                                                        v.isOpen());
                })
                .toList();

        return result.stream().sorted(Comparator.comparingInt(AdminProductStockDataDTO::totalStock)).toList();
    }

    @Test
    @DisplayName(value = "할인중인 상품 리스트")
    void getDiscountProduct() {
        AdminPageDTO pageDTO = new AdminPageDTO(null, 1);
        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                                            , pageDTO.amount()
                                            , Sort.by("updatedAt").descending());
        List<Product> dataList = productList.stream().filter(v -> v.getProductDiscount() > 0).toList();
        Page<Product> result = productRepository.getDiscountProduct(pageDTO, pageable);

        assertNotNull(result);
        assertEquals(dataList.size(), result.getTotalElements());
        assertTrue(result.getContent().size() <= pageDTO.amount());
        result.getContent().forEach(v -> assertTrue(v.getProductDiscount() > 0));
    }

    @Test
    @DisplayName(value = "할인중인 상품 리스트 검색")
    void getDiscountProductSearch() {
        List<Product> dataList = productList.stream().filter(v -> v.getProductDiscount() > 0).toList();
        AdminPageDTO pageDTO = new AdminPageDTO(dataList.get(0).getProductName(), 1);
        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                , pageDTO.amount()
                , Sort.by("updatedAt").descending());

        Page<Product> result = productRepository.getDiscountProduct(pageDTO, pageable);
        Product data = dataList.get(0);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).getProductDiscount() > 0);
        assertEquals(data.getId(), result.getContent().get(0).getId());
    }

    @Test
    @DisplayName(value = "할인중인 상품 리스트 검색. 존재하지 않는 상품 검색")
    void getDiscountProductSearchEmpty() {
        AdminPageDTO pageDTO = new AdminPageDTO("FAIL", 1);
        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                , pageDTO.amount()
                , Sort.by("updatedAt").descending());

        Page<Product> result = productRepository.getDiscountProduct(pageDTO, pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName(value = "상품 분류에 해당하는 상품 리스트 조회")
    void getProductByClassification() {
        List<AdminDiscountProductDTO> result = productRepository.getProductByClassification("OUTER");

        assertNotNull(result);
        assertEquals(PRODUCT_SIZE, result.size());
    }


    @Test
    @DisplayName(value = "상품 분류에 해당하는 상품 리스트 조회. 상품이 없는 경우")
    void getProductByClassificationEmpty() {
        List<AdminDiscountProductDTO> result = productRepository.getProductByClassification("TOP");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName(value = "상품 할인율 수정")
    @Transactional
    void patchProductDiscount() {
        List<String> productIdList = List.of(updateProduct.getId());
        AdminDiscountPatchDTO patchDTO = new AdminDiscountPatchDTO(productIdList, 10);

        assertDoesNotThrow(() -> productRepository.patchProductDiscount(patchDTO));
        Product updateData = productRepository.findById(updateProduct.getId()).orElseThrow(IllegalArgumentException::new);

        assertEquals(10, updateData.getProductDiscount());
    }

    @Test
    @DisplayName(value = "상품아이디 리스트 기반 상품 아이디와 분류 조회")
    void findClassificationAllByProductIds() {
        List<ProductIdClassificationDTO> result = productRepository.findClassificationAllByProductIds(productIds);

        assertNotNull(result);
        result.forEach(v -> assertEquals("OUTER", v.classificationId()));
    }

    @Test
    @DisplayName(value = "상품 판매량 수정")
    void patchProductSalesQuantity() {
        Map<String, Integer> productMap = new HashMap<>();
        long updateSalesQuantity = updateProduct.getProductSalesQuantity() + 100L;
        productMap.put(updateProduct.getId(), 100);

        assertDoesNotThrow(() -> productRepository.patchProductSalesQuantity(productMap));
        Product updateData = productRepository.findById(updateProduct.getId()).orElseThrow(IllegalArgumentException::new);
        assertEquals(updateSalesQuantity, updateData.getProductSalesQuantity());
    }
}
