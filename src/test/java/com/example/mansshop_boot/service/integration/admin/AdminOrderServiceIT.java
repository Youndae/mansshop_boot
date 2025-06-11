package com.example.mansshop_boot.service.integration.admin;

import com.example.mansshop_boot.Fixture.*;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.admin.out.AdminOrderResponseDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.OrderStatus;
import com.example.mansshop_boot.domain.enumeration.RedisCaching;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderRepository;
import com.example.mansshop_boot.service.admin.AdminOrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
@ActiveProfiles("test")
@Transactional
public class AdminOrderServiceIT {

    @Autowired
    private AdminOrderService adminOrderService;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private ClassificationRepository classificationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    private List<ProductOrder> productOrderList;

    @BeforeEach
    void init() {
        MemberAndAuthFixtureDTO memberFixture = MemberAndAuthFixture.createDefaultMember(40);
        List<Member> memberList = memberFixture.memberList();
        List<Auth> authList = memberFixture.authList();
        List<Classification> classificationFixture = ClassificationFixture.createClassification();

        memberRepository.saveAll(memberList);
        authRepository.saveAll(authList);
        classificationRepository.saveAll(classificationFixture);

        List<Product> productFixture = ProductFixture.createSaveProductList(5, classificationFixture.get(0));
        List<ProductOption> productOptionList = productFixture.stream()
                                                .flatMap(v -> v.getProductOptions().stream())
                                                .toList();
        productRepository.saveAll(productFixture);
        productOptionRepository.saveAll(productOptionList);

        productOrderList = ProductOrderFixture.createDefaultProductOrder(memberList, productOptionList);

        productOrderRepository.saveAll(productOrderList);
    }

    @Test
    @DisplayName(value = "전체 주문 목록 조회")
    void getAllOrderList() {
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createDefaultAdminOrderPageDTO();
        int totalPages = (int) Math.ceil((double) productOrderList.size() / pageDTO.amount());
        String cachingKey = RedisCaching.ADMIN_ORDER_COUNT.getKey();

        PagingListDTO<AdminOrderResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminOrderService.getAllOrderList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(productOrderList.size(), result.pagingData().getTotalElements());
        Assertions.assertEquals(totalPages, result.pagingData().getTotalPages());
        Assertions.assertEquals(pageDTO.amount(), result.content().size());
        result.content().forEach(v -> Assertions.assertFalse(v.detailList().isEmpty()));

        Long cachingResult = redisTemplate.opsForValue().get(cachingKey);

        Assertions.assertNotNull(cachingResult);
        Assertions.assertEquals(productOrderList.size(), cachingResult);

        redisTemplate.delete(cachingKey);
    }

    @Test
    @DisplayName(value = "전체 주문 목록 조회. 데이터가 없는 경우")
    void getAllOrderListEmpty() {
        productOrderRepository.deleteAll();
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createDefaultAdminOrderPageDTO();
        String cachingKey = RedisCaching.ADMIN_ORDER_COUNT.getKey();

        PagingListDTO<AdminOrderResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminOrderService.getAllOrderList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.content().isEmpty());
        Assertions.assertEquals(0, result.pagingData().getTotalElements());
        Assertions.assertEquals(0, result.pagingData().getTotalPages());

        Long cachingResult = redisTemplate.opsForValue().get(cachingKey);

        Assertions.assertNotNull(cachingResult);
        Assertions.assertEquals(0, cachingResult);

        redisTemplate.delete(cachingKey);
    }

    @Test
    @DisplayName(value = "전체 주문 목록 조회. 주문자명으로 조회")
    void getAllOrderListSearchRecipient() {
        ProductOrder order = productOrderList.get(0);
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminOrderPageDTO(order.getRecipient(), "recipient", 1);
        String cachingKey = RedisCaching.ADMIN_ORDER_COUNT.getKey();

        PagingListDTO<AdminOrderResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminOrderService.getAllOrderList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertEquals(1, result.pagingData().getTotalElements());
        Assertions.assertEquals(1, result.pagingData().getTotalPages());

        AdminOrderResponseDTO responseDTO = result.content().get(0);

        Assertions.assertEquals(order.getId(), responseDTO.orderId());
        Assertions.assertEquals(order.getRecipient(), responseDTO.recipient());
        Assertions.assertEquals(order.getMember().getUserId(), responseDTO.userId());

        Long cachingResult = redisTemplate.opsForValue().get(cachingKey);

        Assertions.assertNull(cachingResult);
    }

    @Test
    @DisplayName(value = "전체 주문 목록 조회. 사용자 아이디로 조회")
    void getAllOrderListSearchUserId() {
        ProductOrder order = productOrderList.get(0);
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminOrderPageDTO(order.getMember().getUserId(), "userId", 1);
        String cachingKey = RedisCaching.ADMIN_ORDER_COUNT.getKey();

        PagingListDTO<AdminOrderResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminOrderService.getAllOrderList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertEquals(1, result.pagingData().getTotalElements());
        Assertions.assertEquals(1, result.pagingData().getTotalPages());

        AdminOrderResponseDTO responseDTO = result.content().get(0);

        Assertions.assertEquals(order.getId(), responseDTO.orderId());
        Assertions.assertEquals(order.getRecipient(), responseDTO.recipient());
        Assertions.assertEquals(order.getMember().getUserId(), responseDTO.userId());

        Long cachingResult = redisTemplate.opsForValue().get(cachingKey);

        Assertions.assertNull(cachingResult);
    }

    @Test
    @DisplayName(value = "미처리 주문 목록 조회")
    void getNewOrderList() {
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createDefaultAdminOrderPageDTO();
        int totalPages = (int) Math.ceil((double) productOrderList.size() / pageDTO.amount());

        PagingListDTO<AdminOrderResponseDTO> result = Assertions.assertDoesNotThrow(
                () -> adminOrderService.getNewOrderList(pageDTO)
        );

        Assertions.assertNotNull(result);
        Assertions.assertEquals(productOrderList.size(), result.pagingData().getTotalElements());
        Assertions.assertEquals(totalPages, result.pagingData().getTotalPages());
        Assertions.assertEquals(pageDTO.amount(), result.content().size());
        result.content().forEach(v -> Assertions.assertFalse(v.detailList().isEmpty()));
    }

    @Test
    @DisplayName(value = "미처리 주문 목록 조회. 데이터가 없는 경우")
    void getNewOrderListEmpty() {
        productOrderRepository.deleteAll();
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createDefaultAdminOrderPageDTO();

        PagingListDTO<AdminOrderResponseDTO> result = Assertions.assertDoesNotThrow(
                () -> adminOrderService.getNewOrderList(pageDTO)
        );

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.content().isEmpty());
        Assertions.assertEquals(0, result.pagingData().getTotalElements());
        Assertions.assertEquals(0, result.pagingData().getTotalPages());
    }

    @Test
    @DisplayName(value = "미처리 주문 목록 조회. 주문자명으로 조회")
    void getNewOrderListSearchRecipient() {
        ProductOrder order = productOrderList.get(0);
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminOrderPageDTO(order.getRecipient(), "recipient", 1);

        PagingListDTO<AdminOrderResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminOrderService.getAllOrderList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertEquals(1, result.pagingData().getTotalElements());
        Assertions.assertEquals(1, result.pagingData().getTotalPages());

        AdminOrderResponseDTO responseDTO = result.content().get(0);

        Assertions.assertEquals(order.getId(), responseDTO.orderId());
        Assertions.assertEquals(order.getRecipient(), responseDTO.recipient());
        Assertions.assertEquals(order.getMember().getUserId(), responseDTO.userId());
    }

    @Test
    @DisplayName(value = "미처리 주문 목록 조회. 사용자 아이디로 조회")
    void getNewOrderListSearchUserId() {
        ProductOrder order = productOrderList.get(0);
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminOrderPageDTO(order.getMember().getUserId(), "userId", 1);

        PagingListDTO<AdminOrderResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminOrderService.getAllOrderList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertEquals(1, result.pagingData().getTotalElements());
        Assertions.assertEquals(1, result.pagingData().getTotalPages());

        AdminOrderResponseDTO responseDTO = result.content().get(0);

        Assertions.assertEquals(order.getId(), responseDTO.orderId());
        Assertions.assertEquals(order.getRecipient(), responseDTO.recipient());
        Assertions.assertEquals(order.getMember().getUserId(), responseDTO.userId());
    }

    @Test
    @DisplayName(value = "주문 상태 상품 준비중으로 수정")
    void postOrderPreparation() {
        ProductOrder order = productOrderList.get(0);

        String result = Assertions.assertDoesNotThrow(() -> adminOrderService.orderPreparation(order.getId()));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(Result.OK.getResultKey(), result);
        Assertions.assertEquals(OrderStatus.PREPARATION.getStatusStr(), order.getOrderStat());
    }

    @Test
    @DisplayName(value = "주문 상태 상품 준비중으로 수정. 주문 데이터가 없는 경우")
    void postOrderPreparationNotFound() {

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> adminOrderService.orderPreparation(0L)
        );
    }
}
