package com.example.mansshop_boot;

import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.memberQnA.MemberQnARepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderRepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnARepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewRepository;
import com.example.mansshop_boot.repository.productSales.ProductSalesSummaryRepository;
import com.example.mansshop_boot.service.AdminService;
import com.example.mansshop_boot.service.OrderService;
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

    @Autowired
    private ProductSalesSummaryRepository productSalesSummaryRepository;

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Autowired
    private OrderService orderService;

    @Test
    void name() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        /*AdminPageDTO pageDTO = new AdminPageDTO(null, 1);
        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                , pageDTO.amount()
                , Sort.by("classificationStep").ascending());*/
//        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO("테스터3닉네임", "user", 1);
//        List<AdminReviewDTO> result = productReviewRepository.findAllByAdminReviewList(pageDTO, AdminListType.ALL.name());

//        result.forEach(System.out::println);

//        adminService.getFailedMessageList();

        /*for(int i = 0; i < 1; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                productReviewRepository.findAllByAdminReviewList(pageDTO, AdminListType.ALL.name());
            }, executor);

            futures.add(future);
        }*/

//        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
//        executor.shutdown();
        /*AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);
        PagingListDTO<AdminOrderResponseDTO> result = adminService.getNewOrderList(pageDTO);*/

//        System.out.println(result.content().get(0));
    }
}
