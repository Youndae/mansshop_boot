package com.example.mansshop_boot.domain.dto.oAuth;

import com.example.mansshop_boot.domain.entity.Member;

public class OAuth2ResponseEntityConverter {

    public static Member toEntity(OAuth2Response oAuth2Response, String userId) {
        return Member.builder()
                .userId(userId)
                .userEmail(oAuth2Response.getEmail())
                .userName(oAuth2Response.getName())
                .provider(oAuth2Response.getProvider())
                .build();
    }
}
