package com.example.mansshop_boot;

import com.example.mansshop_boot.domain.dto.admin.out.AdminOrderResponseDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminQnAListResponseDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.memberQnA.MemberQnARepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderRepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnARepository;
import com.example.mansshop_boot.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest(classes = MansShopBootApplication.class)
public class QueryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductQnARepository productQnARepository;

    @Autowired
    private AdminService adminService;

    @Autowired
    private MemberQnARepository memberQnARepository;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Test
    void name() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                adminService.getOrderListByDay("2024-05-01", 1);
            }, executor);

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
        /*AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);
        PagingListDTO<AdminOrderResponseDTO> result = adminService.getNewOrderList(pageDTO);*/

//        System.out.println(result.content().get(0));
    }
}
