package com.example.mansshop_boot.domain.dto.admin;

import lombok.Builder;

@Builder
public record AdminQnAClassificationDTO(
        long id
        , String name
) {
}
