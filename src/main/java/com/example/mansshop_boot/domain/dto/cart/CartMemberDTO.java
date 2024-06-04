package com.example.mansshop_boot.domain.dto.cart;

import lombok.Builder;

@Builder
public record CartMemberDTO(
        String uid
        , String cartCookieValue
) {
}
