package com.example.mansshop_boot.auth.oAuth;

import com.example.mansshop_boot.auth.user.CustomUserDetails;
import com.example.mansshop_boot.auth.oAuth.response.OAuth2DTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User, CustomUserDetails {

    private final OAuth2DTO oAuth2DTO;

    public CustomOAuth2User(OAuth2DTO oAuth2DTO) {
        this.oAuth2DTO = oAuth2DTO;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        oAuth2DTO.authList()
                .forEach(val ->
                        authorities.add((GrantedAuthority) val::getAuth)
                );

        return authorities;
    }

    @Override
    public String getName() {
        return oAuth2DTO.username();
    }

    public String getUserId() {
        return oAuth2DTO.userId();
    }

    public String getNickname() {
        return oAuth2DTO.nickname();
    }
}
