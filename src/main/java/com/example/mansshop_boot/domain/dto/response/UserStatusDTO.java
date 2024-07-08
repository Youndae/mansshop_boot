package com.example.mansshop_boot.domain.dto.response;


import lombok.Builder;

@Builder
public record UserStatusDTO(
        boolean loggedIn
        , String uid
) {

    public UserStatusDTO(String nickname) {
        this(nickname != null, nickname);
    }
}
