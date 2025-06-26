package com.example.mansshop_boot.service.admin;

import com.example.mansshop_boot.domain.dto.admin.business.AdminOrderDTO;
import com.example.mansshop_boot.domain.dto.admin.business.AdminOrderDetailDTO;
import com.example.mansshop_boot.domain.dto.admin.business.AdminOrderDetailListDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminOrderResponseDTO;
import com.example.mansshop_boot.domain.dto.cache.CacheRequest;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.PagingMappingDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.entity.ProductOrder;
import com.example.mansshop_boot.domain.enumeration.OrderStatus;
import com.example.mansshop_boot.domain.enumeration.RedisCaching;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.productOrder.ProductOrderDetailRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminOrderServiceImpl implements AdminOrderService{

    private final AdminCacheService adminCacheService;

    private final ProductOrderRepository productOrderRepository;

    private final ProductOrderDetailRepository productOrderDetailRepository;

    /**
     *
     * @param pageDTO
     *
     * 모든 주문 목록 및 상세 데이터 조회.
     * 주문 목록의 경우 단기간에 많이 쌓일 수 있는 데이터이기 때문에 Pageable보다 직접 구현이 더 빠르다는 것을 테스트로 확인.
     * 데이터량이 상대적으로 적거나 증가폭이 낮은 조회에 대해서는 Pageable을 사용해 처리했지만 이렇게 증가폭이 높을 가능성이 있고
     * 주석 작성일 기준 250만개 데이터를 넣어두었기에 직접 구현으로 처리.
     *
     * 클라이언트에서 주문 목록 출력 후 클릭 시 모달창을 통해 상세 정보를 출력하므로
     * 재요청을 하도록 하는 것이 아닌 처음부터 상세 정보까지 같이 담아 반환하도록 처리.
     */
    @Override
    public PagingListDTO<AdminOrderResponseDTO> getAllOrderList(AdminOrderPageDTO pageDTO) {
        List<AdminOrderDTO> orderDTOList = productOrderRepository.findAllOrderList(pageDTO);

        if(orderDTOList.isEmpty()) {
            PagingMappingDTO pagingMappingDTO = new PagingMappingDTO(0L, pageDTO.page(), pageDTO.amount());
            return new PagingListDTO<AdminOrderResponseDTO>(Collections.emptyList(), pagingMappingDTO);
        }

        Long totalElements;
        if(pageDTO.keyword() == null)
            totalElements = adminCacheService.getFullScanCountCache(RedisCaching.ADMIN_ORDER_COUNT, new CacheRequest(pageDTO));
        else
            totalElements = productOrderRepository.findAllOrderListCount(pageDTO);

        return mappingOrderDataAndPagingData(orderDTOList, totalElements, pageDTO);
    }

    /**
     *
     * @param pageDTO
     *
     * 미처리 주문 목록 및 상세 데이터 조회.
     * 요청 당일 16시 이전의 주문건 중 미처리 주문건만 조회해서 반환.
     *
     * 전체 주문 목록과 마찬가지로 모달창으로 처리하므로 상세 데이터까지 매핑해서 반환하도록 처리.
     */
    @Override
    public PagingListDTO<AdminOrderResponseDTO> getNewOrderList(AdminOrderPageDTO pageDTO) {

        LocalDateTime todayLastOrderTime = LocalDateTime.now()
                .withHour(16)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        List<AdminOrderDTO> orderDTOList = productOrderRepository.findAllNewOrderList(pageDTO, todayLastOrderTime);
        Long totalElements = 0L;

        if(!orderDTOList.isEmpty())
            totalElements = productOrderRepository.findAllNewOrderListCount(pageDTO, todayLastOrderTime);

        return mappingOrderDataAndPagingData(orderDTOList, totalElements, pageDTO);
    }

    /**
     *
     * @param orderDTOList
     * @param totalElements
     * @param pageDTO
     *
     * 조회한 주문 목록에 대해 상세 정보 리스트를 조회.
     * 이후 주문 목록에 대한 상세 정보를 매핑 한 뒤 페이징 정보와 같이 반환.
     */
    private PagingListDTO<AdminOrderResponseDTO> mappingOrderDataAndPagingData(List<AdminOrderDTO> orderDTOList, Long totalElements, AdminOrderPageDTO pageDTO) {
        List<Long> orderIdList = orderDTOList.stream().map(AdminOrderDTO::orderId).toList();
        List<AdminOrderDetailListDTO> detailList = productOrderDetailRepository.findByOrderIds(orderIdList);

        List<AdminOrderResponseDTO> responseContent = orderDTOList.stream()
                .map(v -> {
                    List<AdminOrderDetailDTO> detail = detailList.stream()
                            .filter(entity -> v.orderId() == entity.orderId())
                            .map(AdminOrderDetailDTO::new)
                            .toList();

                    return v.toResponseDTO(detail);
                })
                .toList();

        PagingMappingDTO pagingMappingDTO = new PagingMappingDTO(totalElements, pageDTO.page(), pageDTO.amount());

        return new PagingListDTO<>(responseContent, pagingMappingDTO);
    }

    /**
     *
     * @param orderId
     *
     * 주문 상태 주문 확인중 -> 상품 준비중 으로 수정.
     * 관리자가 주문 내역을 확인 후 버튼 클릭 시 수정.
     */
    @Override
    public String orderPreparation(long orderId) {
        ProductOrder productOrder = productOrderRepository.findById(orderId).orElseThrow(IllegalArgumentException::new);
        productOrder.setOrderStat(OrderStatus.PREPARATION.getStatusStr());
        productOrderRepository.save(productOrder);

        return Result.OK.getResultKey();
    }
}
