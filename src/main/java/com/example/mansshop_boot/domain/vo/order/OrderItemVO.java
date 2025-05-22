package com.example.mansshop_boot.domain.vo.order;

public record OrderItemVO(
	String productId,
	Long optionId,
	int count,
	int price
) {

}
