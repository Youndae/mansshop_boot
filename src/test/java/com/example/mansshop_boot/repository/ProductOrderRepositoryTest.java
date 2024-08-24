package com.example.mansshop_boot.repository;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductOrderRepositoryTest {

    /*@Autowired
    private ProductOrderRepository productOrderRepository;

    @Test
    @DisplayName("orderList 조회. 3개월 이내 데이터만 조회.")
    void orderList3TermTest() {

        MemberOrderDTO orderDTO = MemberOrderDTO.builder()
                                    .userId("coco")
                                    .recipient(null)
                                    .phone(null)
                                    .build();

        OrderPageDTO pageDTO = OrderPageDTO.builder()
                .pageNum(1)
                .term("all")
                .build();

        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                            , pageDTO.orderAmount()
                            , Sort.by("orderId").descending());


        Page<ProductOrder> orderPage = productOrderRepository.findByUserId(orderDTO, pageDTO, pageable);

        orderPage.getContent().forEach(v ->
                System.out.println("userId : " + v.getMember().getUserId() +
                        ", recipient : " + v.getRecipient() +
                        ", orderPhone : " + v.getOrderPhone() +
                        ", orderAddr : " + v.getOrderAddress() +
                        ", orderMemo : " + v.getOrderMemo() +
                        ", orderTotalPrice : " + v.getOrderTotalPrice() +
                        ", deliveryFee : " + v.getDeliveryFee() +
                        ", createdAt : " + v.getCreatedAt()));
    }*/
}