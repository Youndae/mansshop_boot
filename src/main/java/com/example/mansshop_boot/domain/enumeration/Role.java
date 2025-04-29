package com.example.mansshop_boot.domain.enumuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    MEMBER("ROLE_MEMBER"),
    ADMIN("ROLE_ADMIN"),
    MANAGER("ROLE_MANAGER");

    private final String key;
}
