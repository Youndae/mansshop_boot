package com.example.mansshop_boot.service.integration;

import com.example.mansshop_boot.Fixture.ClassificationFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.main.out.MainListResponseDTO;
import com.example.mansshop_boot.domain.dto.pageable.MainPageDTO;
import com.example.mansshop_boot.domain.entity.Classification;
import com.example.mansshop_boot.domain.entity.Product;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
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

import java.util.List;

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

    @BeforeEach
    void init() {
        List<Classification> classificationList = ClassificationFixture.createClassification();
        classificationRepository.saveAll(classificationList);

        List<Product> productList = ProductFixture.createSaveProductList(50, classificationList.get(0));
        productRepository.saveAll(productList);
    }
}
