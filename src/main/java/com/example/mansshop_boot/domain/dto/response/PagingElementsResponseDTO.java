package com.example.mansshop_boot.domain.dto.response;

import com.example.mansshop_boot.domain.dto.member.UserStatusDTO;
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

    public PagingElementsResponseDTO(Page<T> pageableResponse, String nickname) {
        this(
                pageableResponse.getContent()
                , pageableResponse.isEmpty()
                , pageableResponse.getNumber()
                , pageableResponse.getTotalPages()
                , pageableResponse.getTotalElements()
                , new UserStatusDTO(nickname)
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
}
