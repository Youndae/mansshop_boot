package com.example.mansshop_boot.domain.dto.admin;

import java.util.List;

public record AdminDiscountPatchDTO(
        List<String> productIdList
        , int discount
) {
}
