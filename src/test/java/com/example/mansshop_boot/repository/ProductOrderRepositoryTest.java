package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.Fixture.*;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.admin.business.AdminOrderDTO;
import com.example.mansshop_boot.domain.dto.mypage.business.MemberOrderDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumuration.OrderStatus;
import com.example.mansshop_boot.domain.enumuration.PageAmount;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderDetailRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = MansShopBootApplication.class)
@ActiveProfiles("test")
public class ProductOrderRepositoryTest {

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

    private static final int PRODUCT_SIZE = 30;

    private Member member;

    private ProductOrder anonymousOrder;

    private List<ProductOrder> orderList;

    private List<ProductOrder> newOrderList;

    @BeforeAll
    void init(){
        MemberAndAuthFixtureDTO memberAndAuthFixture = MemberAndAuthFixture.createDefaultMember(5);
        List<Classification> classificationList = ClassificationFixture.createClassification();
        List<Product> productFixtureList = ProductFixture.createDefaultProductByOUTER(PRODUCT_SIZE);
        List<ProductOption> optionFixtureList = productFixtureList.stream().flatMap(v -> v.getProductOptions().stream()).toList();
        List<Member> memberFixtureList = memberAndAuthFixture.memberList();
        List<Auth> authFixtureList = memberAndAuthFixture.authList();
        MemberAndAuthFixtureDTO anonymousFixture = MemberAndAuthFixture.createAnonymous();
        Member anonymous = anonymousFixture.memberList().get(0);
        memberFixtureList.addAll(anonymousFixture.memberList());
        authFixtureList.addAll(anonymousFixture.authList());

        memberRepository.saveAll(memberFixtureList);
        authRepository.saveAll(memberAndAuthFixture.authList());
        classificationRepository.saveAll(classificationList);
        productRepository.saveAll(productFixtureList);
        productOptionRepository.saveAll(optionFixtureList);

        List<ProductOrder> orderFixtureList = ProductOrderFixture.createDefaultProductOrder(memberFixtureList, optionFixtureList);
        List<ProductOrderDetail> orderDetailFixtureList = new ArrayList<>(orderFixtureList.stream().flatMap(v -> v.getProductOrderDetailSet().stream()).toList());
        List<ProductOrder> anonymousOrderFixtureList = ProductOrderFixture.createAnonymousProductOrder(anonymous, optionFixtureList);
        List<ProductOrderDetail> anonymousOrderDetailFixtureList = anonymousOrderFixtureList.stream()
                                                                                            .flatMap(v ->
                                                                                                    v.getProductOrderDetailSet().stream()
                                                                                            )
                                                                                            .toList();

        orderFixtureList.addAll(anonymousOrderFixtureList);
        orderDetailFixtureList.addAll(anonymousOrderDetailFixtureList);

        productOrderRepository.saveAll(orderFixtureList);
        productOrderDetailRepository.saveAll(orderDetailFixtureList);

        member = memberFixtureList.get(0);
        orderList = orderFixtureList;
        anonymousOrder = anonymousOrderFixtureList.get(0);
        newOrderList = orderList.stream().filter(v -> v.getOrderStat().equals(OrderStatus.ORDER.getStatusStr())).toList();
    }

    @Test
    @DisplayName(value = "사용자의 주문 목록 조회")
    void findByUserId() {
        MemberOrderDTO memberOrderDTO = new MemberOrderDTO(member.getUserId(), null, null);
        OrderPageDTO pageDTO = new OrderPageDTO(1, "3");
        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                                        , pageDTO.orderAmount()
                                        , Sort.by("orderId").descending());
        List<ProductOrder> memberOrder = orderList.stream()
                                                .filter(v ->
                                                        v.getMember().getUserId().equals(member.getUserId())
                                                )
                                                .toList();

        Page<ProductOrder> result = productOrderRepository.findByUserId(memberOrderDTO, pageDTO, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(memberOrder.size(), result.getTotalElements());
        Assertions.assertEquals(memberOrder.get(memberOrder.size() - 1).getId(), result.getContent().get(0).getId());
    }

    @Test
    @DisplayName(value = "비회원의 주문 목록 조회")
    void findByAnonymous() {
        MemberOrderDTO memberOrderDTO = new MemberOrderDTO(null, anonymousOrder.getRecipient(), anonymousOrder.getOrderPhone());
        OrderPageDTO pageDTO = new OrderPageDTO(1, "3");
        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.orderAmount()
                , Sort.by("orderId").descending());

        Page<ProductOrder> result = productOrderRepository.findByUserId(memberOrderDTO, pageDTO, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertEquals(anonymousOrder.getId(), result.getContent().get(0).getId());
    }

    @Test
    @DisplayName(value = "비회원 주문 목록 처럼 처리되나, 회원의 데이터가 전달되는 경우 조회가 안돼야 함.")
    void findByUserIdWrongData() {
        ProductOrder memberOrder = orderList.stream()
                                            .filter(v ->
                                                    v.getMember().getUserId().equals(member.getUserId())
                                            )
                                            .toList().get(0);
        MemberOrderDTO memberOrderDTO = new MemberOrderDTO(null, memberOrder.getRecipient(), memberOrder.getOrderPhone());
        OrderPageDTO pageDTO = new OrderPageDTO(1, "3");
        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.orderAmount()
                , Sort.by("orderId").descending());

        Page<ProductOrder> result = productOrderRepository.findByUserId(memberOrderDTO, pageDTO, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName(value = "관리자의 모든 주문 목록 조회")
    void findAllOrderList() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);

        List<AdminOrderDTO> result = productOrderRepository.findAllOrderList(pageDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(orderList.size(), result.size());
    }

    @Test
    @DisplayName(value = "관리자의 모든 주문 목록 조회. 사용자 아이디 기반 검색")
    void findAllOrderListSearchByUserId() {
        ProductOrder memberOrder = orderList.stream()
                                            .filter(v ->
                                                    v.getMember().getUserId().equals(member.getUserId())
                                            )
                                            .toList().get(0);
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(member.getUserId(), "userId", 1);
        List<AdminOrderDTO> result = productOrderRepository.findAllOrderList(pageDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(memberOrder.getId(), result.get(0).orderId());
    }

    @Test
    @DisplayName(value = "관리자의 모든 주문 목록 조회. 수령인 기반 검색")
    void findAllOrderListSearchByRecipient() {
        ProductOrder memberOrder = orderList.stream()
                .filter(v ->
                        v.getMember().getUserId().equals(member.getUserId())
                )
                .toList().get(0);
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(memberOrder.getRecipient(), "recipient", 1);
        List<AdminOrderDTO> result = productOrderRepository.findAllOrderList(pageDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(memberOrder.getId(), result.get(0).orderId());
    }

    @Test
    @DisplayName(value = "모든 주문 목록 count")
    void findAllOrderListCount() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);
        Long result = productOrderRepository.findAllOrderListCount(pageDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(orderList.size(), result);
    }

    @Test
    @DisplayName(value = "모든 주문 목록 count. 사용자 아이디 기반")
    void findAllOrderListCountSearchByUserId() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(member.getUserId(), "userId", 1);
        Long result = productOrderRepository.findAllOrderListCount(pageDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result);
    }

    @Test
    @DisplayName(value = "모든 주문 목록 count. 수령인 기반")
    void findAllOrderListCountSearchByRecipient() {
        ProductOrder memberOrder = orderList.stream()
                .filter(v ->
                        v.getMember().getUserId().equals(member.getUserId())
                )
                .toList().get(0);
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(memberOrder.getRecipient(), "recipient", 1);
        Long result = productOrderRepository.findAllOrderListCount(pageDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result);
    }

    @Test
    @DisplayName(value = "모든 주문 목록 count. 존재하지 않는 데이터 기반")
    void findAllOrderListCountSearchByEmpty() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO("fakeUser", "userId", 1);
        Long result = productOrderRepository.findAllOrderListCount(pageDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0L, result);
    }

    @Test
    @DisplayName(value = "새로운 주문 목록 조회")
    void findAllNewOrderList() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);
        LocalDateTime todayLastOrderTime = createLastOrderTime();
        List<AdminOrderDTO> result = productOrderRepository.findAllNewOrderList(pageDTO, todayLastOrderTime);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(newOrderList.size(), result.size());
    }

    @Test
    @DisplayName(value = "새로운 주문 목록 조회. 사용자 아이디 기반 검색")
    void findAllNewOrderListSearchByUserId() {
        ProductOrder memberOrder = orderList.stream()
                .filter(v ->
                        v.getMember().getUserId().equals(member.getUserId())
                )
                .toList().get(0);
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(member.getUserId(), "userId", 1);
        LocalDateTime todayLastOrderTime = createLastOrderTime();
        List<AdminOrderDTO> result = productOrderRepository.findAllNewOrderList(pageDTO, todayLastOrderTime);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(memberOrder.getId(), result.get(0).orderId());
    }

    @Test
    @DisplayName(value = "새로운 주문 목록 조회. 수령인 기반 검색")
    void findAllNewOrderListSearchByRecipient() {
        ProductOrder memberOrder = orderList.stream()
                .filter(v ->
                        v.getMember().getUserId().equals(member.getUserId())
                )
                .toList().get(0);
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(memberOrder.getRecipient(), "recipient", 1);
        LocalDateTime todayLastOrderTime = createLastOrderTime();
        List<AdminOrderDTO> result = productOrderRepository.findAllNewOrderList(pageDTO, todayLastOrderTime);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(memberOrder.getId(), result.get(0).orderId());
    }

    @Test
    @DisplayName(value = "새로운 주문 목록 count")
    void findAllNewOrderListCount() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);
        LocalDateTime todayLastOrderTime = createLastOrderTime();
        Long result = productOrderRepository.findAllNewOrderListCount(pageDTO, todayLastOrderTime);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(newOrderList.size(), result);
    }

    @Test
    @DisplayName(value = "새로운 주문 목록 count. 사용자 아이디 기반")
    void findAllNewOrderListCountSearchByUserId() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(member.getUserId(), "userId", 1);
        LocalDateTime todayLastOrderTime = createLastOrderTime();
        Long result = productOrderRepository.findAllNewOrderListCount(pageDTO, todayLastOrderTime);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result);
    }

    @Test
    @DisplayName(value = "새로운 주문 목록 count. 수령인 기반")
    void findAllNewOrderListCountSearchByRecipient() {
        ProductOrder memberOrder = orderList.stream()
                .filter(v ->
                        v.getMember().getUserId().equals(member.getUserId())
                )
                .toList().get(0);
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(memberOrder.getRecipient(), "recipient", 1);
        LocalDateTime todayLastOrderTime = createLastOrderTime();
        Long result = productOrderRepository.findAllNewOrderListCount(pageDTO, todayLastOrderTime);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result);
    }

    @Test
    @DisplayName(value = "새로운 주문 목록 count. 존재하지 않는 데이터 기반")
    void findAllNewOrderListCountSearchByEmpty() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO("fakeUser", "userId", 1);
        LocalDateTime todayLastOrderTime = createLastOrderTime();
        Long result = productOrderRepository.findAllNewOrderListCount(pageDTO, todayLastOrderTime);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0L, result);
    }

    /**
     * ProductOrder의 createdAt에서 @CreationTimestamp를 사용하기 때문에
     * 새로운 주문 목록 조회 시간을 다음날로 설정.
     * @return
     */
    private LocalDateTime createLastOrderTime() {
        return LocalDateTime.now()
                .withHour(16)
                .withMinute(0)
                .withSecond(0)
                .withNano(0).plusDays(1);
    }

    @Test
    @DisplayName(value = "해당 날짜의 모든 주문 목록 조회")
    void findAllByDay() {
        Pageable pageable = PageRequest.of(0
                , PageAmount.ADMIN_DAILY_ORDER_AMOUNT.getAmount()
                , Sort.by("createdAt").descending());
        LocalDate start = LocalDate.now();
        LocalDateTime startDate = LocalDateTime.of(start, LocalTime.MIN);
        LocalDateTime endDate = LocalDateTime.of(start, LocalTime.MAX);

        Page<ProductOrder> result = productOrderRepository.findAllByDay(startDate, endDate, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(orderList.size(), result.getContent().size());
    }
}
