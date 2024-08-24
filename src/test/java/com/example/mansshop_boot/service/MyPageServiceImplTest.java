package com.example.mansshop_boot.service;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MyPageServiceImplTest {

    /*@Autowired
    private MyPageService myPageService;

    @Test
    @DisplayName("사용자의 주문 목록 조회")
    void orderList() {
        OrderPageDTO orderPageDTO = OrderPageDTO.builder()
                .term("3")
                .pageNum(1)
                .build();

        MemberOrderDTO memberOrderDTO = MemberOrderDTO.builder()
                .userId("coco")
                .recipient(null)
                .phone(null)
                .build();

        PagingListDTO<MyPageOrderDTO> responseDTO = myPageService.getOrderList(orderPageDTO, memberOrderDTO);

        responseDTO.content().forEach(System.out::println);

    }*/
}