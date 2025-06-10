package com.example.mansshop_boot.service.unit.admin;

import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.Fixture.ProductOrderFixture;
import com.example.mansshop_boot.domain.dto.admin.business.AdminOrderDTO;
import com.example.mansshop_boot.domain.dto.admin.business.AdminOrderDetailListDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminOrderResponseDTO;
import com.example.mansshop_boot.domain.dto.cache.CacheRequest;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.entity.ProductOption;
import com.example.mansshop_boot.domain.entity.ProductOrder;
import com.example.mansshop_boot.domain.enumeration.RedisCaching;
import com.example.mansshop_boot.repository.productOrder.ProductOrderDetailRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderRepository;
import com.example.mansshop_boot.service.admin.AdminCacheServiceImpl;
import com.example.mansshop_boot.service.admin.AdminOrderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AdminOrderServiceUnitTest {

    @InjectMocks
    private AdminOrderServiceImpl adminService;

    @Mock
    private AdminCacheServiceImpl adminCacheService;

    @Mock
    private ProductOrderRepository productOrderRepository;

    @Mock
    private ProductOrderDetailRepository productOrderDetailRepository;

    @Mock
    private RedisTemplate<String, Long> redisTemplate;

    @Mock
    private ValueOperations<String, Long> valueOperation;

    @Test
    @DisplayName(value = "모든 주문 목록 조회")
    void getAllOrderList() {
        List<Member> memberFixtureList = MemberAndAuthFixture.createDefaultMember(30).memberList();
        List<ProductOption> productOptionFixtureList = ProductFixture.createDefaultProductByOUTER(20)
                .stream()
                .flatMap(v -> v.getProductOptions().stream())
                .toList();
        List<ProductOrder> orderFixtureList = ProductOrderFixture.createSaveProductOrder(memberFixtureList, productOptionFixtureList);
        List<AdminOrderDTO> resultOrderDTO = orderFixtureList.stream()
                .map(v ->
                        new AdminOrderDTO(v.getId(),
                                v.getRecipient(),
                                v.getMember().getUserId(),
                                v.getOrderPhone(),
                                v.getCreatedAt(),
                                v.getOrderAddress(),
                                v.getOrderStat()
                        )
                )
                .limit(20)
                .toList();
        List<AdminOrderDetailListDTO> resultOrderDetailList = orderFixtureList.stream()
                .flatMap(v -> v.getProductOrderDetailSet().stream())
                .toList()
                .stream()
                .map(v ->
                        new AdminOrderDetailListDTO(
                                v.getProductOrder().getId(),
                                v.getProduct().getClassification().getId(),
                                v.getProduct().getProductName(),
                                v.getProductOption().getSize(),
                                v.getProductOption().getColor(),
                                v.getOrderDetailCount(),
                                v.getOrderDetailPrice(),
                                v.isOrderReviewStatus()
                        )
                )
                .toList();

        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);
        List<Long> orderIds = resultOrderDTO.stream().mapToLong(AdminOrderDTO::orderId).boxed().toList();
        when(productOrderRepository.findAllOrderList(pageDTO))
                .thenReturn(resultOrderDTO);
        when(adminCacheService.getFullScanCountCache(any(), any(CacheRequest.class)))
                .thenReturn((long) orderFixtureList.size());
        when(productOrderDetailRepository.findByOrderIds(orderIds))
                .thenReturn(resultOrderDetailList);

        PagingListDTO<AdminOrderResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminService.getAllOrderList(pageDTO));

        verify(adminCacheService).getFullScanCountCache(any(), any(CacheRequest.class));
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertEquals(orderFixtureList.size(), result.pagingData().getTotalElements());
        Assertions.assertEquals(2, result.pagingData().getTotalPages());
        Assertions.assertEquals(pageDTO.amount(), result.content().size());
    }

    @Test
    @DisplayName(value = "모든 주문 목록 조회. 주문 목록이 없는 경우")
    void getAllOrderListEmpty() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);

        when(productOrderRepository.findAllOrderList(pageDTO))
                .thenReturn(Collections.emptyList());
        when(adminCacheService.getFullScanCountCache(RedisCaching.ADMIN_ORDER_COUNT, new CacheRequest(pageDTO)))
                .thenReturn(0L);

        PagingListDTO<AdminOrderResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminService.getAllOrderList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.content().isEmpty());
        Assertions.assertEquals(0, result.pagingData().getTotalElements());
        Assertions.assertEquals(0, result.pagingData().getTotalPages());
    }

    @Test
    @DisplayName(value = "미처리 주문 목록 조회")
    void getNewOrderList() {
        List<Member> memberFixtureList = MemberAndAuthFixture.createDefaultMember(30).memberList();
        List<ProductOption> productOptionFixtureList = ProductFixture.createDefaultProductByOUTER(20)
                .stream()
                .flatMap(v -> v.getProductOptions().stream())
                .toList();
        List<ProductOrder> orderFixtureList = ProductOrderFixture.createSaveProductOrder(memberFixtureList, productOptionFixtureList);
        List<AdminOrderDTO> resultOrderDTO = orderFixtureList.stream()
                .map(v ->
                        new AdminOrderDTO(v.getId(),
                                v.getRecipient(),
                                v.getMember().getUserId(),
                                v.getOrderPhone(),
                                v.getCreatedAt(),
                                v.getOrderAddress(),
                                v.getOrderStat()
                        )
                )
                .limit(20)
                .toList();
        List<AdminOrderDetailListDTO> resultOrderDetailList = orderFixtureList.stream()
                .flatMap(v -> v.getProductOrderDetailSet().stream())
                .toList()
                .stream()
                .map(v ->
                        new AdminOrderDetailListDTO(
                                v.getProductOrder().getId(),
                                v.getProduct().getClassification().getId(),
                                v.getProduct().getProductName(),
                                v.getProductOption().getSize(),
                                v.getProductOption().getColor(),
                                v.getOrderDetailCount(),
                                v.getOrderDetailPrice(),
                                v.isOrderReviewStatus()
                        )
                )
                .toList();

        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);
        List<Long> orderIds = resultOrderDTO.stream().mapToLong(AdminOrderDTO::orderId).boxed().toList();
        LocalDateTime todayLastOrderTime = LocalDateTime.now()
                .withHour(16)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        when(productOrderRepository.findAllNewOrderList(pageDTO, todayLastOrderTime))
                .thenReturn(resultOrderDTO);
        when(productOrderRepository.findAllNewOrderListCount(pageDTO, todayLastOrderTime))
                .thenReturn((long) orderFixtureList.size());
        when(productOrderDetailRepository.findByOrderIds(orderIds))
                .thenReturn(resultOrderDetailList);

        PagingListDTO<AdminOrderResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminService.getNewOrderList(pageDTO));

        System.out.println("result : " + result);
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertEquals(orderFixtureList.size(), result.pagingData().getTotalElements());
        Assertions.assertEquals(2, result.pagingData().getTotalPages());
        Assertions.assertEquals(pageDTO.amount(), result.content().size());
    }

    @Test
    @DisplayName(value = "미처리 주문 목록 조회. 주문 목록이 없는 경우")
    void getNewOrderListEmpty() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);
        LocalDateTime todayLastOrderTime = LocalDateTime.now()
                .withHour(16)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        when(productOrderRepository.findAllNewOrderList(pageDTO, todayLastOrderTime))
                .thenReturn(Collections.emptyList());
        when(productOrderRepository.findAllNewOrderListCount(pageDTO, todayLastOrderTime))
                .thenReturn(0L);

        PagingListDTO<AdminOrderResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminService.getAllOrderList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.content().isEmpty());
        Assertions.assertEquals(0, result.pagingData().getTotalElements());
        Assertions.assertEquals(0, result.pagingData().getTotalPages());
    }
}
