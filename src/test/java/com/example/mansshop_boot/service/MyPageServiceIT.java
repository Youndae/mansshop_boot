package com.example.mansshop_boot.service;

import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.mypage.business.MemberOrderDTO;
import com.example.mansshop_boot.domain.dto.mypage.business.MyPagePageDTO;
import com.example.mansshop_boot.domain.dto.mypage.out.*;
import com.example.mansshop_boot.domain.dto.mypage.qna.out.*;
import com.example.mansshop_boot.domain.dto.pageable.LikePageDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.security.Principal;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
public class MyPageServiceIT {

    @Autowired
    private MyPageService myPageService;

    @Test
    @DisplayName(value = "사용자의 주문 리스트 조회")
    void getOrderList() {
        OrderPageDTO pageDTO = new OrderPageDTO(1, "all");
        MemberOrderDTO memberOrderDTO = new MemberOrderDTO("tester2", null, null);

        PagingListDTO<MyPageOrderDTO> result = myPageService.getOrderList(pageDTO, memberOrderDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(pageDTO.orderAmount(), result.content().size());
    }

    @Test
    @DisplayName(value = "관심 상품 리스트 조회")
    void getLikeList() {
        LikePageDTO pageDTO = new LikePageDTO(1);
        Principal principal = createPrincipal();

        Page<ProductLikeDTO> result = myPageService.getLikeList(pageDTO, principal);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(pageDTO.likeAmount(), result.getContent().size());
    }

    @Test
    @DisplayName(value = "사용자가 작성한 상품 문의 목록 조회")
    void getProductQnAList() {
        MyPagePageDTO pageDTO = new MyPagePageDTO(1);
        Principal principal = createPrincipal();

        Page<ProductQnAListDTO> result = myPageService.getProductQnAList(pageDTO, principal);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(pageDTO.amount(), result.getContent().size());
    }

    @Test
    @DisplayName(value = "작성한 상품 문의 상세 조회")
    void getProductQnADetail() {
        long productQnAId = 162L;
        Principal principal = createPrincipal();

        ProductQnADetailDTO result = myPageService.getProductQnADetail(productQnAId, principal);

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName(value = "회원 문의 목록 조회")
    void getMemberQnAList() {
        MyPagePageDTO pageDTO = new MyPagePageDTO(1);
        Principal principal = createPrincipal();

        Page<MemberQnAListDTO> result = myPageService.getMemberQnAList(pageDTO, principal);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(pageDTO.amount(), result.getContent().size());

    }

    @Test
    @DisplayName(value = "회원 문의 상세 조회")
    void getMemberQnADetail() {
        long memberQnAId = 162L;
        Principal principal = createPrincipal();

        MemberQnADetailDTO result = myPageService.getMemberQnADetail(memberQnAId, principal);

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName(value = "회원 문의 수정 데이터 요청")
    void getMemberQnAPatchData() {
        long memberQnAId = 162L;
        Principal principal = createPrincipal();

        MemberQnAModifyDataDTO result = myPageService.getModifyData(memberQnAId, principal);

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName(value = "작성한 리뷰 목록 조회")
    void getReviewList() {
        MyPagePageDTO pageDTO = new MyPagePageDTO(1);
        Principal principal = createPrincipal();

        Page<MyPageReviewDTO> result = myPageService.getReview(pageDTO, principal);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(pageDTO.amount(), result.getContent().size());
    }

    @Test
    @DisplayName(value = "리뷰 수정 데이터 요청")
    void getReviewPatchData() {
        long reviewId = 645L;
        Principal principal = createPrincipal();

        MyPagePatchReviewDataDTO result = myPageService.getPatchReview(reviewId, principal);

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName(value = "정보 수정을 위한 사용자 데이터 조회")
    void getUserInfo() {
        Principal principal = createPrincipal();

        MyPageInfoDTO result = myPageService.getInfo(principal);
        Assertions.assertNotNull(result);
    }



    Principal createPrincipal() {
        return () -> "tester2";
    }
}
