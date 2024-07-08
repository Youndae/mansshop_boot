package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.mypage.MemberOrderDTO;
import com.example.mansshop_boot.domain.dto.mypage.MyPageOrderDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MyPageServiceImplTest {

    @Autowired
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

    }
}