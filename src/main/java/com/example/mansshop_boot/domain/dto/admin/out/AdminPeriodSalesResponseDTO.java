package com.example.mansshop_boot.domain.dto.admin.out;

import java.util.List;

public record AdminPeriodSalesResponseDTO <T>(
        List<T> content,
        long sales,
        long salesQuantity,
        long orderQuantity
) {
}
