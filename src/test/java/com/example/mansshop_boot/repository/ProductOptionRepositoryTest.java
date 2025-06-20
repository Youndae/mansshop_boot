package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.Fixture.ClassificationFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.admin.business.AdminOptionStockDTO;
import com.example.mansshop_boot.domain.dto.admin.business.AdminProductOptionDTO;
import com.example.mansshop_boot.domain.dto.order.business.OrderProductInfoDTO;
import com.example.mansshop_boot.domain.dto.order.in.OrderProductDTO;
import com.example.mansshop_boot.domain.dto.product.business.ProductOptionDTO;
import com.example.mansshop_boot.domain.entity.Classification;
import com.example.mansshop_boot.domain.entity.Product;
import com.example.mansshop_boot.domain.entity.ProductOption;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = MansShopBootApplication.class)
@ActiveProfiles("test")
public class ProductOptionRepositoryTest {

    @Autowired
    private ClassificationRepository classificationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    private static final int PRODUCT_SIZE = 30;

    private Product product;

    private List<Product> productList;

    @BeforeAll
    void init(){
        List<Classification> classificationList = ClassificationFixture.createClassification();
        List<Product> productFixtureList = ProductFixture.createDefaultProductByOUTER(PRODUCT_SIZE);
        List<ProductOption> optionList = productFixtureList.stream().flatMap(v -> v.getProductOptions().stream()).toList();

        classificationRepository.saveAll(classificationList);
        productRepository.saveAll(productFixtureList);
        productOptionRepository.saveAll(optionList);

        productList = productFixtureList;
        product = productFixtureList.get(0);
    }

    @Test
    @DisplayName(value = "상품 아이디 기반 옵션 데이터 조회. isOpen == true만 조회")
    void findByDetailOption() {
        List<ProductOptionDTO> result = productOptionRepository.findByDetailOption(product.getId());
        List<Long> optionIds = product.getProductOptions()
                                        .stream()
                                        .filter(ProductOption::isOpen)
                                        .mapToLong(ProductOption::getId)
                                        .boxed()
                                        .toList();
        assertNotNull(result);
        assertEquals(optionIds.size(), result.size());
        result.forEach(v -> assertTrue(optionIds.contains(v.optionId())));
    }

    @Test
    @DisplayName(value = "상품 아이디 기반 옵션 데이터 조회. isOpen과 상관없이 모두 조회")
    void findAllByProductId() {
        List<AdminProductOptionDTO> result = productOptionRepository.findAllByProductId(product.getId());
        List<Long> optionIds = product.getProductOptions()
                                        .stream()
                                        .mapToLong(ProductOption::getId)
                                        .boxed()
                                        .toList();
        assertNotNull(result);
        assertEquals(product.getProductOptions().size(), result.size());
        result.forEach(v -> assertTrue(optionIds.contains(v.optionId())));
    }

    @Test
    @DisplayName(value = "productId 리스트 기반 옵션 데이터 조회")
    void findAllOptionByProductIdList() {
        List<String> productIds = productList.stream().map(Product::getId).toList();
        List<Long> optionIds = productList.stream()
                                            .flatMapToLong(v ->
                                                    v.getProductOptions()
                                                        .stream()
                                                        .mapToLong(ProductOption::getId)
                                            )
                                            .boxed()
                                            .toList();
        List<AdminOptionStockDTO> result = productOptionRepository.findAllOptionByProductIdList(productIds);

        assertNotNull(result);
        assertEquals(optionIds.size(), result.size());
        result.forEach(v -> assertTrue(productIds.contains(v.productId())));
    }



    @Test
    @DisplayName(value = "상품 아이디 기반 옵션 데이터 조회. DTO 매핑이 아닌 엔티티 리스트로 반환")
    void findAllOptionByProductId() {
        List<ProductOption> result = productOptionRepository.findAllOptionByProductId(product.getId());
        List<Long> optionIds = product.getProductOptions().stream().mapToLong(ProductOption::getId).boxed().toList();
        assertNotNull(result);
        assertEquals(product.getProductOptions().size(), result.size());
        result.forEach(v -> assertTrue(optionIds.contains(v.getId())));
    }

    @Test
    @DisplayName(value = "옵션 아이디 리스트 기반 상품 정보 및 옵션 데이터 조회")
    void findOrderData() {
        List<Long> optionIds = product.getProductOptions().stream().mapToLong(ProductOption::getId).boxed().toList();
        List<OrderProductInfoDTO> result = productOptionRepository.findOrderData(optionIds);
        int productDiscountPrice = product.getProductPrice() - (product.getProductPrice() * product.getProductDiscount() / 100);
        assertNotNull(result);
        assertEquals(optionIds.size(), result.size());
        for(OrderProductInfoDTO data : result) {
            assertEquals(product.getId(), data.productId());
            assertEquals(productDiscountPrice, data.price());
            assertTrue(optionIds.contains(data.optionId()));
        }
    }

    @Test
    @DisplayName(value = "주문된 상품 옵션의 재고 수정")
    void patchOrderStock() {
        List<OrderProductDTO> orderProductList = new ArrayList<>();
        Random ran = new Random();
        for(ProductOption option : product.getProductOptions()){
            int detailCount = ran.nextInt(4) + 1;
            int detailPrice = product.getProductPrice() * detailCount;
            int stock = Math.max((option.getStock() - detailCount), 0);
            option.setStock(stock);

            orderProductList.add(
                    new OrderProductDTO(option.getId(),
                                        product.getProductName(),
                                        product.getId(),
                                        detailCount,
                                        detailPrice
                                )
            );
        }

        assertDoesNotThrow(() -> productOptionRepository.patchOrderStock(orderProductList));

        List<ProductOption> patchOptionList = productOptionRepository.findAllOptionByProductId(product.getId());

        for(ProductOption patchData : patchOptionList) {
            for(ProductOption data : product.getProductOptions()) {
                if(data.getId() == patchData.getId()) {
                    assertEquals(data.getStock(), patchData.getStock());
                    break;
                }
            }
        }
    }
}
