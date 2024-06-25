package com.example.mansshop_boot.domain.dto.admin;

import com.example.mansshop_boot.domain.dto.member.UserStatusDTO;

import java.util.List;

public record AdminPeriodSalesResponseDTO(
        List<?> content
        , long sales
        , long salesQuantity
        , long orderQuantity
) {
}
