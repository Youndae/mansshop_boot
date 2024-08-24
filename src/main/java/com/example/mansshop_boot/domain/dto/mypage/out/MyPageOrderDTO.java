package com.example.mansshop_boot.domain.dto.mypage.out;

import com.example.mansshop_boot.domain.dto.mypage.business.MyPageOrderDetailDTO;
import com.example.mansshop_boot.domain.entity.ProductOrder;

import java.time.LocalDateTime;
import java.util.List;


public record MyPageOrderDTO(
        long orderId
        , int orderTotalPrice
        , LocalDateTime orderDate
        , String orderStat
        , List<MyPageOrderDetailDTO> detail
) {

    public MyPageOrderDTO(ProductOrder data, List<MyPageOrderDetailDTO> detail) {
        this(
                data.getId()
                , data.getOrderTotalPrice()
                , data.getCreatedAt()
                , data.getOrderStat()
                , detail
        );
    }
}
