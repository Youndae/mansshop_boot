package com.example.mansshop_boot.domain.dto.member;


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
