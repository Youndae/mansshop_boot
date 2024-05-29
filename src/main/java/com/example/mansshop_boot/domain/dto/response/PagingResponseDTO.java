package com.example.mansshop_boot.domain.dto.response;

import com.example.mansshop_boot.domain.dto.member.UserStatusDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public record PagingResponseDTO<T>(
        List<T> content
        , boolean empty
        , long number
        , long totalPages
        , UserStatusDTO userStatus
) {

    public PagingResponseDTO(Page<T> pageableResponse, String nickname){
        this(
                pageableResponse.getContent()
                , pageableResponse.isEmpty()
                , pageableResponse.getNumber()
                , pageableResponse.getTotalPages()
                , new UserStatusDTO(nickname)
        );
    }
}
