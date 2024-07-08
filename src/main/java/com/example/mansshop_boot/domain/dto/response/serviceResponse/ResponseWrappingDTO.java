package com.example.mansshop_boot.domain.dto.response.serviceResponse;

public record ResponseWrappingDTO <T>(
        T content
) {
}
