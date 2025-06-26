package com.example.mansshop_boot.controller.admin;

import com.example.mansshop_boot.Fixture.CartFixture;
import com.example.mansshop_boot.Fixture.ClassificationFixture;
import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.controller.fixture.TokenFixture;
import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.order.business.*;
import com.example.mansshop_boot.domain.dto.order.in.OrderProductDTO;
import com.example.mansshop_boot.domain.dto.order.in.PaymentDTO;
import com.example.mansshop_boot.domain.dto.rabbitMQ.FailedQueueDTO;
import com.example.mansshop_boot.domain.dto.rabbitMQ.RabbitMQProperties;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.RabbitMQPrefix;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.cart.CartRepository;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.periodSales.PeriodSalesSummaryRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderDetailRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderRepository;
import com.example.mansshop_boot.repository.productSales.ProductSalesSummaryRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AdminFailedDataControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TokenFixture tokenFixture;

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
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private ProductOrderDetailRepository productOrderDetailRepository;

    @Autowired
    private PeriodSalesSummaryRepository periodSalesSummaryRepository;

    @Autowired
    private ProductSalesSummaryRepository productSalesSummaryRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("#{jwt['token.access.header']}")
    private String accessHeader;

    @Value("#{jwt['token.refresh.header']}")
    private String refreshHeader;

    @Value("#{jwt['cookie.ino.header']}")
    private String inoHeader;

    private Map<String, String> tokenMap;

    private String accessTokenValue;

    private String refreshTokenValue;

    private String inoValue;

    private Member member;

    private Product product;

    private List<ProductOption> productOptionList;

    private Cart memberCart;

    private List<CartDetail> memberCartDetailList;

    private static final String URL_PREFIX = "/api/admin/";

    @BeforeEach
    void init() {
        MemberAndAuthFixtureDTO memberAndAuthFixtureDTO = MemberAndAuthFixture.createDefaultMember(1);
        MemberAndAuthFixtureDTO adminFixture = MemberAndAuthFixture.createAdmin();
        List<Member> saveMemberList = new ArrayList<>(memberAndAuthFixtureDTO.memberList());
        saveMemberList.addAll(adminFixture.memberList());
        List<Auth> saveAuthList = new ArrayList<>(adminFixture.authList());
        saveAuthList.addAll(adminFixture.authList());
        memberRepository.saveAll(saveMemberList);
        authRepository.saveAll(saveAuthList);

        member = memberAndAuthFixtureDTO.memberList().get(0);
        Member admin = adminFixture.memberList().get(0);

        tokenMap = tokenFixture.createAndSaveAllToken(admin);
        accessTokenValue = tokenMap.get(accessHeader);
        refreshTokenValue = tokenMap.get(refreshHeader);
        inoValue = tokenMap.get(inoHeader);

        List<Classification> classificationList = ClassificationFixture.createClassification();
        classificationRepository.saveAll(classificationList);

        product = ProductFixture.createSaveProductList(1, classificationList.get(0)).get(0);
        productOptionList = product.getProductOptions();
        productRepository.save(product);
        productOptionRepository.saveAll(productOptionList);

        memberCart = CartFixture.createDefaultMemberCart(List.of(member), productOptionList).get(0);
        cartRepository.save(memberCart);

        memberCartDetailList = memberCart.getCartDetailList();
    }

    @AfterEach
    void cleanUP() {
        String accessKey = tokenMap.get("accessKey");
        String refreshKey = tokenMap.get("refreshKey");

        redisTemplate.delete(accessKey);
        redisTemplate.delete(refreshKey);

        cartRepository.deleteAll();
        productOrderRepository.deleteAll();
        memberRepository.deleteAll();
        productSalesSummaryRepository.deleteAll();
        productOptionRepository.deleteAll();
        productRepository.deleteAll();
        classificationRepository.deleteAll();
        periodSalesSummaryRepository.deleteAll();

        List<String> dlqNames = getDLQNames();
        dlqNames.forEach(v -> rabbitAdmin.purgeQueue(v, false));

        await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(200, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    for(String dlq : dlqNames) {
                        Properties queueProperties = rabbitAdmin.getQueueProperties(dlq);
                        Integer messageCount = (Integer) queueProperties.get("QUEUE_MESSAGE_COUNT");
                        assertEquals(0, messageCount);
                    }
                });

        try {
            Thread.sleep(5000);
        }catch (Exception e) {
            e.printStackTrace();
            fail("thread sleep exception");
        }
    }

    private <T> void sendMessage(String exchange, RabbitMQPrefix rabbitMQPrefix, T data) {
        rabbitTemplate.convertAndSend(
                exchange,
                getQueueRoutingKey(rabbitMQPrefix),
                data
        );
    }

    private String getQueueRoutingKey(RabbitMQPrefix rabbitMQPrefix) {
        return rabbitMQProperties.getQueue()
                .get(rabbitMQPrefix.getKey())
                .getDlqRouting();
    }

    private String getOrderExchange() {
        return rabbitMQProperties.getExchange()
                .get(RabbitMQPrefix.EXCHANGE_ORDER.getKey())
                .getDlq();
    }

    private ProductOrderDataDTO createOrderDataDTO(PaymentDTO paymentDTO, CartMemberDTO cartMemberDTO, LocalDateTime createdAt) {
        ProductOrder productOrder = paymentDTO.toOrderEntity(cartMemberDTO.uid(), createdAt);
        List<OrderProductDTO> orderProductList = paymentDTO.orderProduct();
        List<String> orderProductIds = new ArrayList<>();
        List<Long> orderOptionIds = new ArrayList<>();
        int totalProductCount = 0;

        for(OrderProductDTO data : paymentDTO.orderProduct()) {
            productOrder.addDetail(data.toOrderDetailEntity());
            if(!orderProductIds.contains(data.getProductId()))
                orderProductIds.add(data.getProductId());
            orderOptionIds.add(data.getOptionId());
            totalProductCount += data.getDetailCount();
        }
        productOrder.setProductCount(totalProductCount);

        return new ProductOrderDataDTO(productOrder, orderProductList, orderProductIds, orderOptionIds);
    }

    private List<OrderProductDTO> createCartOrderProductDTOFixtureList() {
        List<OrderProductDTO> resultList = new ArrayList<>();
        for(int i = 0; i < memberCartDetailList.size(); i++) {
            ProductOption option = memberCartDetailList.get(i).getProductOption();

            resultList.add(
                    new OrderProductDTO(
                            option.getId(),
                            option.getProduct().getProductName(),
                            option.getProduct().getId(),
                            i + 1,
                            option.getProduct().getProductPrice() * (i + 1)
                    )
            );
        }

        return resultList;
    }

    private List<String> getDLQNames() {
        return rabbitMQProperties.getQueue().values().stream().map(RabbitMQProperties.Queue::getDlq).toList();
    }

    @Test
    @DisplayName(value = "DLQ 메시지 개수 조회")
    void getFailedQueueCount() throws Exception {
        String orderExchange = getOrderExchange();
        List<OrderProductDTO> orderProductList = createCartOrderProductDTOFixtureList();
        int totalPrice = orderProductList.stream().mapToInt(OrderProductDTO::getDetailPrice).sum();
        int deliveryFee = totalPrice < 100000 ? 3500 : 0;
        PaymentDTO paymentDTO = new PaymentDTO(
                "testRecipient",
                "01000010002",
                "testRecipient Memo",
                "testRecipient home",
                orderProductList,
                deliveryFee,
                totalPrice,
                "cash",
                "cart"
        );
        CartMemberDTO cartMemberDTO = new CartMemberDTO(member.getUserId(), null);
        ProductOrderDataDTO productOrderDataDTO = createOrderDataDTO(paymentDTO, cartMemberDTO, LocalDateTime.now());
        ProductOrder order = productOrderDataDTO.productOrder();
        Map<String, RabbitMQProperties.Queue> rabbitMQMap = rabbitMQProperties.getQueue();
        List<String> testQueueNames = List.of(
                rabbitMQMap.get(RabbitMQPrefix.QUEUE_ORDER_CART.getKey()).getDlq(),
                rabbitMQMap.get(RabbitMQPrefix.QUEUE_ORDER_PRODUCT_OPTION.getKey()).getDlq(),
                rabbitMQMap.get(RabbitMQPrefix.QUEUE_ORDER_PRODUCT.getKey()).getDlq(),
                rabbitMQMap.get(RabbitMQPrefix.QUEUE_PERIOD_SUMMARY.getKey()).getDlq(),
                rabbitMQMap.get(RabbitMQPrefix.QUEUE_PRODUCT_SUMMARY.getKey()).getDlq()
        );
        List<FailedQueueDTO> fixtureFailedQueueDTO = rabbitMQProperties.getQueue()
                .values()
                .stream()
                .map(RabbitMQProperties.Queue::getDlq)
                .filter(testQueueNames::contains)
                .map(v -> new FailedQueueDTO(v, 1))
                .toList();

        sendMessage(orderExchange, RabbitMQPrefix.QUEUE_ORDER_CART, new OrderCartDTO(cartMemberDTO, productOrderDataDTO.orderOptionIds()));
        sendMessage(orderExchange, RabbitMQPrefix.QUEUE_ORDER_PRODUCT_OPTION, new OrderProductMessageDTO(productOrderDataDTO));
        sendMessage(orderExchange, RabbitMQPrefix.QUEUE_ORDER_PRODUCT, new OrderProductMessageDTO(productOrderDataDTO));
        sendMessage(orderExchange, RabbitMQPrefix.QUEUE_PERIOD_SUMMARY, new PeriodSummaryQueueDTO(order));
        sendMessage(orderExchange, RabbitMQPrefix.QUEUE_PRODUCT_SUMMARY, new OrderProductSummaryDTO(productOrderDataDTO));

        await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(200, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    MvcResult result = mockMvc.perform(get(URL_PREFIX + "message")
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue)))
                            .andExpect(status().isOk())
                            .andReturn();
                    String content = result.getResponse().getContentAsString();
                    List<FailedQueueDTO> response = om.readValue(
                            content,
                            new TypeReference<>() {}
                    );

                    assertNotNull(response);
                    assertFalse(response.isEmpty());
                    assertEquals(fixtureFailedQueueDTO.size(), response.size());
                    fixtureFailedQueueDTO.forEach(v -> assertTrue(response.contains(v)));
                });
    }

    @Test
    @DisplayName(value = "DLQ 메시지 개수 조회. DLQ 메시지가 전혀 없는 경우")
    void getFailedQueueCountEmpty() throws Exception {
        await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(200, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    MvcResult result = mockMvc.perform(get(URL_PREFIX + "message")
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue)))
                            .andExpect(status().isOk())
                            .andReturn();
                    String content = result.getResponse().getContentAsString();
                    List<FailedQueueDTO> response = om.readValue(
                            content,
                            new TypeReference<>() {}
                    );

                    assertNotNull(response);
                    assertTrue(response.isEmpty());
                });
    }
}
