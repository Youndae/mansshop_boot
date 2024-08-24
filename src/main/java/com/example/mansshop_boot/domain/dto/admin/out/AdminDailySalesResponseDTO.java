package com.example.mansshop_boot.domain.dto.admin.out;

import com.example.mansshop_boot.domain.dto.admin.business.AdminDailySalesDetailDTO;
import com.example.mansshop_boot.domain.entity.ProductOrder;

import java.util.List;

public record AdminDailySalesResponseDTO(
        long totalPrice
        , long deliveryFee
        , String paymentType
        , List<AdminDailySalesDetailDTO> detailList
) {

    public AdminDailySalesResponseDTO(ProductOrder productOrder, List<AdminDailySalesDetailDTO> detailContent) {
        this(
                productOrder.getOrderTotalPrice()
                , productOrder.getDeliveryFee()
                , productOrder.getPaymentType()
                , detailContent
        );
    }
}
