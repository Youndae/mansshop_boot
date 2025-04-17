package com.example.mansshop_boot.domain.dto.order.business;

import com.example.mansshop_boot.domain.dto.order.in.OrderProductDTO;
import lombok.*;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderProductSummaryDTO{

        private List<OrderProductDTO> orderProductDTOList;
        private List<String> productIds;
        private List<Long> productOptionIds;
        private LocalDate periodMonth;


    public OrderProductSummaryDTO(ProductOrderDataDTO productOrderDataDTO) {
        this.orderProductDTOList = productOrderDataDTO.orderProductList();
        this.productIds = productOrderDataDTO.orderProductIds();
        this.productOptionIds = productOrderDataDTO.orderOptionIds();
        this.periodMonth = LocalDate.now().withDayOfMonth(1);
    }
}
