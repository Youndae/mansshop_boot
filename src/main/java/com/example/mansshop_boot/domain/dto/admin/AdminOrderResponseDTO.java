package com.example.mansshop_boot.domain.dto.admin;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record AdminOrderResponseDTO(
        long orderId
        , String recipient
        , String userId
        , String phone
        , LocalDateTime createdAt
        , String address
        , String orderStatus
        , List<AdminOrderDetailDTO> detailList
) {
}
