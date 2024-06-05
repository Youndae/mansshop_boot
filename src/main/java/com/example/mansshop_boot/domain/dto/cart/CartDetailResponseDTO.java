package com.example.mansshop_boot.domain.dto.cart;

import com.example.mansshop_boot.domain.dto.member.UserStatusDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record CartDetailResponseDTO(
        String productName
        , String productThumbnail
        , List<CartDetailOptionDTO> optionList
        , UserStatusDTO userStatus
) {
}
