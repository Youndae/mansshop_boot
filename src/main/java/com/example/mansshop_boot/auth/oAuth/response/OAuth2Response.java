package com.example.mansshop_boot.auth.oAuth.response;

public interface OAuth2Response {

    String getProvider();

    String getProviderId();

    String getEmail();

    String getName();
}
