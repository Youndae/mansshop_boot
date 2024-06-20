package com.example.mansshop_boot.domain.dto.mypage;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MyPageOrderDTO(
        long orderId
        , int orderTotalPrice
        , LocalDateTime orderDate
        , int orderStat
        , List<MyPageOrderDetailDTO> detail
) {
}
