package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.Fixture.ClassificationFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.entity.Classification;
import com.example.mansshop_boot.domain.entity.Product;
import com.example.mansshop_boot.domain.entity.ProductOption;
import com.example.mansshop_boot.domain.entity.ProductThumbnail;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.product.ProductThumbnailRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MansShopBootApplication.class)
@ActiveProfiles("test")
@Transactional
public class ProductThumbnailRepositoryTest {

    @Autowired
    private ClassificationRepository classificationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private ProductThumbnailRepository productThumbnailRepository;

    private static final int PRODUCT_SIZE = 30;

    private Product product;

    @BeforeEach
    void init(){
        List<Classification> classificationList = ClassificationFixture.createClassification();
        List<Product> productFixtureList = ProductFixture.createDefaultProductByOUTER(PRODUCT_SIZE);
        List<ProductOption> optionList = productFixtureList.stream().flatMap(v -> v.getProductOptions().stream()).toList();
        List<ProductThumbnail> thumbnailList = productFixtureList.stream().flatMap(v -> v.getProductThumbnails().stream()).toList();
        classificationRepository.saveAll(classificationList);
        productRepository.saveAll(productFixtureList);
        productOptionRepository.saveAll(optionList);
        productThumbnailRepository.saveAll(thumbnailList);

        product = productFixtureList.get(0);
    }

    @Test
    @DisplayName(value = "상품 아이디 기반 썸네일명 리스트 조회")
    void findByProductId() {
        List<String> result = productThumbnailRepository.findByProductId(product.getId());
        List<String> thumbnailNameList = product.getProductThumbnails().stream().map(ProductThumbnail::getImageName).toList();
        assertNotNull(result);
        result.forEach(v -> assertTrue(thumbnailNameList.contains(v)));
    }

    @Test
    @DisplayName(value = "썸네일명 리스트 기반 데이터 삭제")
    void deleteByImageName() {
        List<String> thumbnailNameList = product.getProductThumbnails().stream().map(ProductThumbnail::getImageName).toList();

        assertDoesNotThrow(() -> productThumbnailRepository.deleteByImageName(thumbnailNameList));
        List<String> result = productThumbnailRepository.findByProductId(product.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
