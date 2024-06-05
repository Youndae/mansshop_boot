package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class ProductProductOrderRepositoryTest {

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("order, orderDetail save 테스트")
    @Transactional
    void orderSave() {
        ProductOrder productOrder = ProductOrder.builder()
                .member(
                        Member.builder()
                                .userId("coco")
                                .build()
                )
                .recipient("정코코")
                .orderPhone("010-1234-5678")
                .orderAddress("우리집이용")
                .orderMemo("테스트 메모")
                .orderTotalPrice(15000)
                .deliveryFee(3500)
                .paymentType("cash")
                .build();


        productOrder.addDetail(
                ProductOrderDetail.builder()
                        .productOption(
                                ProductOption.builder()
                                        .id(1L)
                                        .build()
                        )
                        .product(
                                Product.builder()
                                        .id("BAGS20210629135245")
                                        .build()
                        )
                        .orderDetailCount(1)
                        .orderDetailPrice(1000)
                        .build()
        );

        productOrder.addDetail(
                ProductOrderDetail.builder()
                        .productOption(
                                ProductOption.builder()
                                        .id(2L)
                                        .build()
                        )
                        .product(
                                Product.builder()
                                        .id("BAGS20210629135440")
                                        .build()
                        )
                        .orderDetailCount(1)
                        .orderDetailPrice(200)
                        .build()
        );

        productOrder.addDetail(
                ProductOrderDetail.builder()
                        .productOption(
                                ProductOption.builder()
                                        .id(3L)
                                        .build()
                        )
                        .product(
                                Product.builder()
                                        .id("BAGS20210629140117")
                                        .build()
                        )
                        .orderDetailCount(1)
                        .orderDetailPrice(300)
                        .build()
        );


        long saveId = productOrderRepository.save(productOrder).getId();

        ProductOrder productOrderData = productOrderRepository.findById(saveId).orElse(null);

        System.out.println("orderId : " + productOrderData.getId());
        System.out.println("orderId : " + productOrderData.getMember().getUserId());
        System.out.println("orderId : " + productOrderData.getRecipient());
        System.out.println("orderId : " + productOrderData.getOrderPhone());
        System.out.println("orderId : " + productOrderData.getOrderAddress());
        System.out.println("orderId : " + productOrderData.getOrderMemo());
        System.out.println("orderId : " + productOrderData.getOrderTotalPrice());
        System.out.println("orderId : " + productOrderData.getDeliveryFee());
        System.out.println("orderId : " + productOrderData.getPaymentType());

        productOrderData.getProductOrderDetailSet().forEach(v ->
                System.out.println("order Detail Set : "
                + v.getProduct().getId() + " : "
                        + v.getProductOption().getId() + " : "
                        + v.getOrderDetailCount() + " : "
                        + v.getOrderDetailPrice()
                )
        );



    }
}