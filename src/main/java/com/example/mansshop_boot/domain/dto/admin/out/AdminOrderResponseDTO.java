package com.example.mansshop_boot.domain.dto.admin.out;

import com.example.mansshop_boot.domain.dto.admin.business.AdminOrderDetailDTO;
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
