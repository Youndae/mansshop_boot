package com.example.mansshop_boot.domain.dto.oAuth;

public interface OAuth2Response {

    String getProvider();

    String getProviderId();

    String getEmail();

    String getName();
}
