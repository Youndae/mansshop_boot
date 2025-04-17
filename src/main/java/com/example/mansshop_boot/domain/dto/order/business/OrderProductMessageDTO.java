package com.example.mansshop_boot.domain.dto.order.business;

import com.example.mansshop_boot.domain.dto.order.in.OrderProductDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderProductMessageDTO{

    private List<OrderProductDTO> orderProductList;


    public OrderProductMessageDTO(ProductOrderDataDTO dto) {
        this.orderProductList = dto.orderProductList();
    }
}
