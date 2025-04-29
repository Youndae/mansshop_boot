package com.example.mansshop_boot.domain.dto.response;

import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public record PagingElementsResponseDTO<T>(
        List<T> content,
        boolean empty,
        long number,
        long totalPages,
        long totalElements
) {

    public PagingElementsResponseDTO(Page<T> pageableResponse) {
        this(
                pageableResponse.getContent(),
                pageableResponse.isEmpty(),
                pageableResponse.getNumber(),
                pageableResponse.getTotalPages(),
                pageableResponse.getTotalElements()
        );
    }

    public PagingElementsResponseDTO(PagingListDTO<T> dto) {
        this(
                dto.content(),
                dto.pagingData().isEmpty(),
                dto.pagingData().getNumber(),
                dto.pagingData().getTotalPages(),
                dto.pagingData().getTotalElements()
        );
    }
}
