package com.example.mansshop_boot.domain.dto.response;

public record ResponseDTO<T>(
        T content
        , UserStatusDTO userStatus
) {

    public ResponseDTO(T content, UserStatusDTO userStatus) {
        this.content = content;
        this.userStatus = userStatus;
    }
}
