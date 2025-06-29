package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.Fixture.ClassificationFixture;
import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.Fixture.ProductOrderFixture;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.admin.business.AdminOrderDetailListDTO;
import com.example.mansshop_boot.domain.dto.mypage.business.MyPageOrderDetailDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.OrderStatus;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MansShopBootApplication.class)
@ActiveProfiles("test")
@Transactional
public class ProductOrderDetailRepositoryTest {

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

    @BeforeEach
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
    @DisplayName(value = "주문 아이디 리스트 기반 조회. 마이페이지 주문 목록 조회 중 상세 데이터 조회")
    void findByDetailList() {
        List<ProductOrder> orderDataList = IntStream.of(0, 6)
                                    .mapToObj(v -> orderList.get(v))
                                    .toList();
        List<Long> orderIds = orderDataList.stream().mapToLong(ProductOrder::getId).boxed().toList();
        List<ProductOrderDetail> orderDetailDataList = orderDataList.stream()
                                                                .flatMap(v ->
                                                                        v.getProductOrderDetailSet().stream()
                                                                )
                                                                .toList();
        List<MyPageOrderDetailDTO> result = productOrderDetailRepository.findByDetailList(orderIds);

        assertNotNull(result);
        assertEquals(orderDetailDataList.size(), result.size());

        for(MyPageOrderDetailDTO resultData : result) {
            boolean flag = false;
            for(ProductOrderDetail detailData : orderDetailDataList) {
                if(resultData.detailId() == detailData.getId()) {
                    assertEquals(resultData.orderId(), detailData.getProductOrder().getId());
                    assertEquals(resultData.productId(), detailData.getProduct().getId());
                    assertEquals(resultData.optionId(), detailData.getProductOption().getId());
                    assertEquals(resultData.productName(), detailData.getProduct().getProductName());
                    assertEquals(resultData.size(), detailData.getProductOption().getSize());
                    assertEquals(resultData.color(), detailData.getProductOption().getColor());
                    assertEquals(resultData.detailCount(), detailData.getOrderDetailCount());
                    assertEquals(resultData.detailPrice(), detailData.getOrderDetailPrice());

                    flag = true;
                    break;
                }
            }
            assertTrue(flag);
        }
    }

    @Test
    @DisplayName(value = "주문 아이디 리스트 기반 상세 데이터 조회. 관리자 주문 상세 조회")
    void findByOrderIds() {
        List<ProductOrder> orderDataList = IntStream.of(0, 6)
                .mapToObj(v -> orderList.get(v))
                .toList();
        List<Long> orderIds = orderDataList.stream().mapToLong(ProductOrder::getId).boxed().toList();
        List<ProductOrderDetail> orderDetailDataList = orderDataList.stream()
                .flatMap(v ->
                        v.getProductOrderDetailSet().stream()
                )
                .toList();
        List<AdminOrderDetailListDTO> result = productOrderDetailRepository.findByOrderIds(orderIds);

        assertNotNull(result);
        assertEquals(orderDetailDataList.size(), result.size());

        for(ProductOrder order : orderDataList) {
            int count = 0;
            for(AdminOrderDetailListDTO resultData : result) {
                if(order.getId().equals(resultData.orderId()))
                    count++;
            }

            assertEquals(order.getProductOrderDetailSet().size(), count);
        }
    }
}
