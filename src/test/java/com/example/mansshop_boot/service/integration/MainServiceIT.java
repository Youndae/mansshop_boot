package com.example.mansshop_boot.service.integration;

import com.example.mansshop_boot.Fixture.ClassificationFixture;
import com.example.mansshop_boot.Fixture.PageDTOFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.Fixture.util.PaginationUtils;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.main.business.MainListDTO;
import com.example.mansshop_boot.domain.dto.main.out.MainListResponseDTO;
import com.example.mansshop_boot.domain.dto.pageable.MainPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.entity.Classification;
import com.example.mansshop_boot.domain.entity.Product;
import com.example.mansshop_boot.domain.entity.ProductOption;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.service.MainService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
@ActiveProfiles("test")
@Transactional
public class MainServiceIT {

    @Autowired
    private MainService mainService;

    @Autowired
    private ClassificationRepository classificationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    private List<Product> productList;

    private List<Classification> classificationList;

    @BeforeEach
    void init() {
        classificationList = ClassificationFixture.createClassification();
        classificationRepository.saveAll(classificationList);

        productList = ProductFixture.createSaveProductList(50, classificationList.get(0));
        List<ProductOption> optionList = productList.stream()
                .flatMap(v -> v.getProductOptions().stream())
                .toList();
        productRepository.saveAll(productList);
        productOptionRepository.saveAll(optionList);
    }

    @Test
    @DisplayName(value = "BEST 상품 리스트 조회")
    void getBestList() {
        MainPageDTO pageDTO = PageDTOFixture.createDefaultMainPageDTO("BEST");
        List<Product> fixtureList = productList.stream()
                .sorted(
                        Comparator.comparingLong(Product::getProductSalesQuantity)
                        .reversed()
                )
                .limit(12)
                .toList();

        List<MainListResponseDTO> result = assertDoesNotThrow(() -> mainService.getBestAndNewList(pageDTO));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(fixtureList.size(), result.size());

        for(int i = 0; i < result.size(); i++) {
            Product fixture = fixtureList.get(i);
            MainListResponseDTO resultDTO = result.get(i);
            int discountPrice = (int) (fixture.getProductPrice() * (1 - ((double) fixture.getProductDiscount() / 100)));
            int stock = fixture.getProductOptions().stream().mapToInt(ProductOption::getStock).sum();

            assertEquals(fixture.getId(), resultDTO.productId());
            assertEquals(fixture.getProductName(), resultDTO.productName());
            assertEquals(fixture.getThumbnail(), resultDTO.thumbnail());
            assertEquals(fixture.getProductPrice(), resultDTO.originPrice());
            assertEquals(fixture.getProductDiscount(), resultDTO.discount());
            assertEquals(discountPrice, resultDTO.discountPrice());
            assertEquals(stock == 0, resultDTO.isSoldOut());
        }
    }

    @Test
    @DisplayName(value = "NEW 상품 리스트 조회")
    void getNewList() {
        MainPageDTO pageDTO = PageDTOFixture.createDefaultMainPageDTO("NEW");

        List<MainListResponseDTO> result = assertDoesNotThrow(() -> mainService.getBestAndNewList(pageDTO));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(12, result.size());
    }

    @Test
    @DisplayName(value = "BEST 상품 리스트 조회. 데이터가 없는 경우")
    void getBestListEmpty() {
        productRepository.deleteAll();
        MainPageDTO pageDTO = PageDTOFixture.createDefaultMainPageDTO("BEST");

        List<MainListResponseDTO> result = assertDoesNotThrow(() -> mainService.getBestAndNewList(pageDTO));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName(value = "상품 분류 리스트 조회")
    void getClassificationList() {
        String classificationId = classificationList.get(0).getId();
        MainPageDTO pageDTO = PageDTOFixture.createDefaultMainPageDTO(classificationId);
        List<Product> fixtureList = productList.stream()
                .filter(v -> v.getClassification().getId().equals(classificationId))
                .toList();
        int contentSize = Math.min(fixtureList.size(), pageDTO.mainProductAmount());
        int totalPages = PaginationUtils.getTotalPages(fixtureList.size(), pageDTO.mainProductAmount());

        PagingListDTO<MainListResponseDTO> result = assertDoesNotThrow(() -> mainService.getClassificationAndSearchList(pageDTO));

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals(contentSize, result.content().size());
        assertEquals(fixtureList.size(), result.pagingData().getTotalElements());
        assertEquals(totalPages, result.pagingData().getTotalPages());
        assertFalse(result.pagingData().isEmpty());
    }

    @Test
    @DisplayName(value = "상품 분류 리스트 조회. 데이터가 없는 경우")
    void getClassificationListEmpty() {
        String classificationId = "noneId";
        MainPageDTO pageDTO = PageDTOFixture.createDefaultMainPageDTO(classificationId);

        PagingListDTO<MainListResponseDTO> result = assertDoesNotThrow(() -> mainService.getClassificationAndSearchList(pageDTO));

        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertEquals(0, result.pagingData().getTotalElements());
        assertEquals(0, result.pagingData().getTotalPages());
        assertTrue(result.pagingData().isEmpty());
    }
}
