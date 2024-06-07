package com.example.mansshop_boot.domain.dto.mypage;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record MyPageOrderDTO(
        long orderId
        , int orderTotalPrice
        , LocalDate orderDate
        , int orderStat
        , List<MyPageOrderDetailDTO> detail
) {
}
