package com.example.mansshop_boot.domain.dto.admin;

import java.util.List;

public record AdminPeriodSalesResponseDTO(
        List<?> content
        , long sales
        , long salesQuantity
        , long orderQuantity
) {
}
