package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.admin.AdminBestSalesProductDTO;
import com.example.mansshop_boot.domain.dto.admin.AdminClassificationSalesDTO;
import com.example.mansshop_boot.domain.dto.admin.AdminPeriodClassificationDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductOrderDetailDSLRepositoryImplTest {

    @Autowired
    private ProductOrderDetailRepository productOrderDetailRepository;

    @Test
    @DisplayName("당월 주문 내역 중 가장 많이 판매된 5개의 상품 리스트")
    void getPeriodBestProduct() {

        LocalDateTime startDate = LocalDateTime.of(2024, 6, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 6, 30, 0, 0);

        List<AdminBestSalesProductDTO> dtoList = productOrderDetailRepository.findPeriodBestProduct(startDate, endDate);

        dtoList.forEach(System.out::println);
    }

    @Test
    @DisplayName("당월 상품 분류별 매출 조회")
    void getPeriodClassification() {

        LocalDateTime startDate = LocalDateTime.of(2024, 6, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 6, 30, 0, 0);

        List<AdminPeriodClassificationDTO> list = productOrderDetailRepository.findPeriodClassification(startDate, endDate);

        list.forEach(System.out::println);
    }

}