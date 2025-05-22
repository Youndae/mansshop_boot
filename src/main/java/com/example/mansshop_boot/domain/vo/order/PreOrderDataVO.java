package com.example.mansshop_boot.domain.vo.order;

import java.util.List;

public record PreOrderDataVO(
	String userId,
	List<OrderItemVO> orderData,
	int totalPrice
) {

}
