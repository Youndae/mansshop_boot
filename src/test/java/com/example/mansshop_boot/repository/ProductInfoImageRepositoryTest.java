package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.Fixture.ClassificationFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.product.ProductInfoImageRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = MansShopBootApplication.class)
@ActiveProfiles("test")
public class ProductInfoImageRepositoryTest {

    @Autowired
    private ClassificationRepository classificationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private ProductInfoImageRepository productInfoImageRepository;

    private static final int PRODUCT_SIZE = 30;

    private Product product;

    @BeforeAll
    void init(){
        List<Classification> classificationList = ClassificationFixture.createClassification();
        List<Product> productFixtureList = ProductFixture.createDefaultProductByOUTER(PRODUCT_SIZE);
        List<ProductOption> optionList = productFixtureList.stream().flatMap(v -> v.getProductOptions().stream()).toList();
        List<ProductInfoImage> infoImageList = productFixtureList.stream().flatMap(v -> v.getProductInfoImages().stream()).toList();
        classificationRepository.saveAll(classificationList);
        productRepository.saveAll(productFixtureList);
        productOptionRepository.saveAll(optionList);
        productInfoImageRepository.saveAll(infoImageList);

        product = productFixtureList.get(0);
    }

    @Test
    @DisplayName(value = "상품 아이디 기반 썸네일명 리스트 조회")
    void findByProductId() {
        List<String> result = productInfoImageRepository.findByProductId(product.getId());
        List<String> infoImageList = product.getProductInfoImages().stream().map(ProductInfoImage::getImageName).toList();
        assertNotNull(result);
        result.forEach(v -> assertTrue(infoImageList.contains(v)));
    }

    @Test
    @DisplayName(value = "썸네일명 리스트 기반 데이터 삭제")
    void deleteByImageName() {
        List<String> infoImageNameList = product.getProductInfoImages().stream().map(ProductInfoImage::getImageName).toList();

        assertDoesNotThrow(() -> productInfoImageRepository.deleteByImageName(infoImageNameList));
        List<String> result = productInfoImageRepository.findByProductId(product.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
