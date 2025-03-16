package com.example.mansshop_boot.domain.dto.order.business;

import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;

import java.util.List;

public record OrderCartDTO(
        CartMemberDTO cartMemberDTO,
        List<Long> productOptionIds
) {
}
