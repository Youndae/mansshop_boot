package com.example.mansshop_boot.domain.dto.order.business;

import com.example.mansshop_boot.domain.dto.order.in.OrderProductDTO;

import java.time.LocalDate;
import java.util.List;

public record OrderProductSummaryDTO(
        List<OrderProductDTO> orderProductDTOList,
        List<String> productIds,
        List<Long> productOptionIds,
        LocalDate periodMonth
) {
    public OrderProductSummaryDTO(ProductOrderDataDTO productOrderDataDTO) {
        this(
                productOrderDataDTO.orderProductList(),
                productOrderDataDTO.orderProductIds(),
                productOrderDataDTO.orderOptionIds(),
                LocalDate.now()
        );
    }
}
