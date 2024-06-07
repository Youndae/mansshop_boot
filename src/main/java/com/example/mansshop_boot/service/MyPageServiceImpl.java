package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.mypage.MemberOrderDTO;
import com.example.mansshop_boot.domain.dto.mypage.MyPageOrderDTO;
import com.example.mansshop_boot.domain.dto.mypage.MyPageOrderDetailDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.entity.ProductOrder;
import com.example.mansshop_boot.repository.ProductOrderDetailRepository;
import com.example.mansshop_boot.repository.ProductOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyPageServiceImpl implements MyPageService{

    private final ProductOrderDetailRepository productOrderDetailRepository;

    private final ProductOrderRepository productOrderRepository;

    private final PrincipalService principalService;

    @Override
    public ResponseEntity<?> getOrderList(OrderPageDTO pageDTO, MemberOrderDTO memberOrderDTO) {

        /*
            data

            pageable,
            content : [
                        {
                            orderId
                            orderTotalPrice
                            orderCreatedAt
                            orderStat
                            detail : [
                                        {
                                            orderId
                                            detailId
                                            productName
                                            size
                                            color
                                            detailCount
                                            detailPrice
                                            reviewStatus
                                            thumbnail
                                        }
                            ]
                        }
            ]
         */

        /*
            둘다 orderId 기준 역정렬 해서 가져올 것.
         */

        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                                            , pageDTO.orderAmount()
                                            , Sort.by("orderId").descending());

        Page<ProductOrder> order = productOrderRepository.findByUserId(memberOrderDTO, pageable);
        List<Long> orderIdList = new ArrayList<>();
        order.getContent().forEach(val -> orderIdList.add(val.getId()));

        List<MyPageOrderDetailDTO> detailDTOList = productOrderDetailRepository.findByDetailList(orderIdList);
        List<MyPageOrderDTO> contentList = new ArrayList<>();
        List<MyPageOrderDetailDTO> orderDetailList = new ArrayList<>();
        for(ProductOrder data : order.getContent()){
            long orderId = data.getId();

            for(int i = 0; i < detailDTOList.size(); i++) {
                if(orderId == detailDTOList.get(i).orderId())
                    orderDetailList.add(detailDTOList.get(i));
            }

            contentList.add(
                    MyPageOrderDTO.builder()
                            .orderId(orderId)
                            .orderTotalPrice(data.getOrderTotalPrice())
                            .orderDate(data.getCreatedAt())
                            .orderStat(data.getOrderStat())
                            .detail(orderDetailList)
                            .build()
            );

            orderDetailList = new ArrayList<>();
        }

        String nickname = principalService.getUidByUserId(memberOrderDTO.userId());

        System.out.println("MyPageService getOrderList :: createNickname");

        PagingResponseDTO<MyPageOrderDTO> responseDTO = new PagingResponseDTO<>(contentList, order.isEmpty(), order.getNumber(), order.getTotalPages(), nickname);

        System.out.println("MyPageService getOrderList :: createResponseDTO");

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }



}
