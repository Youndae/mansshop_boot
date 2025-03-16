package com.example.mansshop_boot;

import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.order.in.OrderProductDTO;
import com.example.mansshop_boot.domain.dto.order.in.PaymentDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumuration.OAuthProvider;
import com.example.mansshop_boot.domain.enumuration.OrderStatus;
import com.example.mansshop_boot.domain.enumuration.Role;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.cart.CartRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderDetailRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderRepository;
import com.example.mansshop_boot.repository.productSales.ProductSalesSummaryRepository;
import com.example.mansshop_boot.service.AdminService;
import com.example.mansshop_boot.service.OrderService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest(classes = MansShopBootApplication.class)
//@EnableJpaRepositories(basePackages = "com.example")
//@DataJpaTest
//@EntityScan(basePackages = "com.example.mansshop_boot.domain.entity")
//@Transactional
//@Rollback(value = false)
public class QueryTest {

    /*@Autowired
    private ProductOrderDetailRepository repository;

    @Autowired
    private AdminService service;

    @Autowired
    private ProductOrderRepository orderRepository;*/

    /*@Test
    @DisplayName("BEST Test")
    @Transactional
    void BEST() {

        String productId = "BAGS20240629205621";
//        AdminPageDTO pageDTO = new AdminPageDTO(null, 1);
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
//        repository.findById(10593269L);
//        System.out.println("find  ids end");

        List<Long> ids = orderRepository.findAllOrderList(pageDTO).stream().map(AdminOrderDTO::orderId).toList();

        System.out.println("ids1");
        repository.findByOrderIds(ids);
        System.out.println("ids1 end");
        System.out.println("ids2");
        System.out.println("ids2 end");
        String term = "2023-01";
        String[] termSplit = term.split("-");
        int year = Integer.parseInt(termSplit[0]);
        int month = Integer.parseInt(termSplit[1]);

        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1);

//        service.getPeriodSalesDetail("2023-01");

//        orderRepository.findPeriodStatistics(startDate, endDate);
//        orderRepository.findPeriodStatistics(startDate, endDate);
//        orderRepository.findPeriodStatistics(startDate, endDate);

        for(int i = 0; i < 50; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//                service.getSalesByDay("2023-01-01");
                orderRepository.findPeriodDailyList(startDate, endDate);
            }, executor);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
    }*/

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderService orderService;

    @Test
    void orderDummy() {
        /*Product product1 = Product.builder()
                                .id("testProduct3")
                                .classification(Classification.builder().id("OUTER").build())
                                .productName("testProduct3Name")
                                .productPrice(5000)
                                .thumbnail("testProduct3Thumb")
                                .isOpen(true)
                                .productSalesQuantity(0L)
                                .productDiscount(0)
                                .build();

        Product product2 = Product.builder()
                .id("testProduct4")
                .classification(Classification.builder().id("OUTER").build())
                .productName("testProduct4Name")
                .productPrice(7000)
                .thumbnail("testProduct4Thumb")
                .isOpen(true)
                .productSalesQuantity(0L)
                .productDiscount(0)
                .build();

        Product product3 = Product.builder()
                .id("testProduct5")
                .classification(Classification.builder().id("OUTER").build())
                .productName("testProduct5Name")
                .productPrice(9000)
                .thumbnail("testProduct5Thumb")
                .isOpen(true)
                .productSalesQuantity(0L)
                .productDiscount(0)
                .build();


        List<Product> productList = List.of(product1, product2, product3);

        ProductOption option1 = ProductOption.builder()
                                            .product(product1)
                                            .size("S")
                                            .color("Black")
                                            .stock(0)
                                            .isOpen(true)
                                            .build();

        ProductOption option2 = ProductOption.builder()
                .product(product1)
                .size("S")
                .color("white")
                .stock(0)
                .isOpen(true)
                .build();

        ProductOption option3 = ProductOption.builder()
                .product(product2)
                .size("FREE")
                .color("Black")
                .stock(0)
                .isOpen(true)
                .build();

        ProductOption option4 = ProductOption.builder()
                .product(product2)
                .size("XL")
                .color("Black")
                .stock(0)
                .isOpen(true)
                .build();

        ProductOption option5 = ProductOption.builder()
                .product(product3)
                .size("M")
                .color("Black")
                .stock(0)
                .isOpen(true)
                .build();

        ProductOption option6 = ProductOption.builder()
                .product(product3)
                .size("M")
                .color("white")
                .stock(0)
                .isOpen(true)
                .build();

        List<ProductOption> options = List.of(option1, option2, option3, option4, option5, option6);

        productRepository.saveAll(productList);
        productOptionRepository.saveAll(options);*/

        Member member = memberRepository.findByUserId("tester1");

        Cart cart = Cart.builder()
                .member(member)
                .cookieId(null)
                .build();

        CartDetail detail1 = CartDetail.builder()
                .productOption(ProductOption.builder().id(5L).build())
                .cartCount(3)
                .build();

        CartDetail detail2 = CartDetail.builder()
                .productOption(ProductOption.builder().id(6L).build())
                .cartCount(5)
                .build();

        CartDetail detail3 = CartDetail.builder()
                .productOption(ProductOption.builder().id(7L).build())
                .cartCount(1)
                .build();

        CartDetail detail4 = CartDetail.builder()
                .productOption(ProductOption.builder().id(9L).build())
                .cartCount(2)
                .build();

        List<CartDetail> detailList = List.of(detail1, detail2, detail3, detail4);
        detailList.forEach(cart::addCartDetail);

        cartRepository.save(cart);
    }

    @Test
    void orderTest() {
        CartMemberDTO cartMemberDTO = new CartMemberDTO("tester1", null);

        OrderProductDTO order1 = new OrderProductDTO(
                5L,
                "testProduct3Name",
                "testProduct3",
                3,
                15000
        );

        OrderProductDTO order2 = new OrderProductDTO(
                6L,
                "testProduct3Name",
                "testProduct3",
                5,
                25000
        );

        OrderProductDTO order3 = new OrderProductDTO(
                7L,
                "testProduct4Name",
                "testProduct4",
                2,
                14000
        );

        OrderProductDTO order4 = new OrderProductDTO(
                9L,
                "testProduct5Name",
                "testProduct5",
                1,
                9000
        );

        List<OrderProductDTO> orderList = List.of(order1, order2, order3, order4);

        PaymentDTO paymentDTO = new PaymentDTO(
                "tester1Name2",
                "010-1234-1234",
                "tester Memo",
                "tester Address",
                orderList,
                3500,
                63000,
                "card",
                "cart",
                11
        );

        orderService.payment(paymentDTO, cartMemberDTO);

        try {
            Thread.sleep(10000);
        }catch (Exception e) {
            System.out.println("test method Thread Error");
            e.printStackTrace();
        }

        /**
         * 1차 테스트 결과
         * order 데이터 정상 detail 데이터 정상
         *
         * option id 5 의 재고가 -1이 되면 안됨.
         *
         * product id 1, 2, 3의 salesQuantity 정상 처리.
         * period의 17000 확인
         * productSummary의 option id 1, 2, 3, 5 정상 처리 확인.
         */
    }

    @Autowired
    private ProductSalesSummaryRepository productSalesSummaryRepository;
    @Test
    void productSalesSummaryTest() {
        LocalDate date = LocalDate.now();
        List<Long> optionIds = List.of(1L, 2L, 3L);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for(int i = 0; i < 100; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                productSalesSummaryRepository.findAllByProductOptionIds(date, optionIds);
            }, executor);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
    }
}
