package com.example.mansshop_boot;

import com.example.mansshop_boot.domain.dto.admin.business.AdminOrderDTO;
import com.example.mansshop_boot.domain.dto.admin.business.AdminProductStockDataDTO;
import com.example.mansshop_boot.domain.dto.mypage.business.MemberOrderDTO;
import com.example.mansshop_boot.domain.dto.pageable.*;
import com.example.mansshop_boot.domain.enumuration.AdminListType;
import com.example.mansshop_boot.repository.*;
import com.example.mansshop_boot.service.AdminService;
import com.example.mansshop_boot.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
public class QueryTest {

    @Autowired
    private ProductOrderDetailRepository repository;

    @Autowired
    private AdminService service;

    @Autowired
    private ProductOrderRepository orderRepository;

    @Test
    @DisplayName("BEST Test")
    @Transactional
    void BEST() {
        /**
         * 문제 포인트
         * findPeriodStatics -> 기간 매출 집계.
         * findPeriodBestProduct -> 베스트 5 상품 ( 상품 매출 집계 )
         * findPeriodClassification -> 분류별 매출 집계
         * getProductSalesList -> 상품 총 매출 집계.
         */
        String productId = "BAGS20240629205621";
        AdminPageDTO pageDTO = new AdminPageDTO(null, 1);
//        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
//        repository.findById(10593269L);
//        System.out.println("find  ids end");

        /*List<Long> ids = orderRepository.findAllOrderList(pageDTO).stream().map(AdminOrderDTO::orderId).toList();

        System.out.println("ids1");
        repository.findByOrderIds(ids);
        System.out.println("ids1 end");
        System.out.println("ids2");
        repository.findByOrderIds2(ids);
        System.out.println("ids2 end");*/
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
    }



}
