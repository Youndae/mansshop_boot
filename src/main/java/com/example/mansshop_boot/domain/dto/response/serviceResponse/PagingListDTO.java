package com.example.mansshop_boot.domain.dto.response.serviceResponse;

import com.example.mansshop_boot.domain.dto.pageable.PagingMappingDTO;

import java.util.List;

public record PagingListDTO <T>(
        List<T> content,
        PagingMappingDTO pagingData
) {
}
