package com.example.mansshop_boot.domain.dto.order.business;

import com.example.mansshop_boot.domain.dto.order.in.OrderProductDTO;
import com.example.mansshop_boot.domain.entity.ProductOrder;

import java.util.List;

public record ProductOrderDataDTO(
        ProductOrder productOrder,
        List<OrderProductDTO> orderProductList,
        List<String> orderProductIds,
        List<Long> orderOptionIds
){
}
