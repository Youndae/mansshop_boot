package com.example.mansshop_boot.domain.dto.oAuth;

import com.example.mansshop_boot.domain.entity.Auth;
import lombok.Builder;

import java.util.List;

@Builder
public record OAuth2DTO(
        String userId
        , String username
        , List<Auth> authList
        , String nickname
) {
}
