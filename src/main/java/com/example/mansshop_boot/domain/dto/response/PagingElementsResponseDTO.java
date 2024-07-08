package com.example.mansshop_boot.domain.dto.response;

import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public record PagingElementsResponseDTO<T>(
        List<T> content
        , boolean empty
        , long number
        , long totalPages
        , long totalElements
        , UserStatusDTO userStatus
) {

    public PagingElementsResponseDTO(Page<T> pageableResponse, UserStatusDTO userStatusDTO) {
        this(
                pageableResponse.getContent()
                , pageableResponse.isEmpty()
                , pageableResponse.getNumber()
                , pageableResponse.getTotalPages()
                , pageableResponse.getTotalElements()
                , userStatusDTO
        );
    }

    public PagingElementsResponseDTO(List<T> content
                                    , boolean isEmpty
                                    , long number
                                    , long totalPages
                                    , long totalElements
                                    , String nickname) {
        this(
                content
                , isEmpty
                , number
                , totalPages
                , totalElements
                , new UserStatusDTO(nickname)
        );
    }

    public PagingElementsResponseDTO(PagingListDTO<T> dto, UserStatusDTO userStatusDTO) {
        this(
                dto.content()
                , dto.pagingData().isEmpty()
                , dto.pagingData().getNumber()
                , dto.pagingData().getTotalPages()
                , dto.pagingData().getTotalElements()
                , userStatusDTO
        );
    }
}
