package com.example.mansshop_boot.domain.dto.oAuth;

import com.example.mansshop_boot.domain.entity.Auth;
import com.example.mansshop_boot.domain.entity.Member;
import lombok.Builder;

import java.util.List;


public record OAuth2DTO(
        String userId
        , String username
        , List<Auth> authList
        , String nickname
) {

    public OAuth2DTO(Member existsData) {
        this(
                existsData.getUserId()
                , existsData.getUserName()
                , existsData.getAuths()
                , existsData.getNickname()
        );
    }
}
