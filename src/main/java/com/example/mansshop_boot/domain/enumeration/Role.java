package com.example.mansshop_boot.domain.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public enum Role {

    MEMBER("ROLE_MEMBER", "member"),
    ADMIN("ROLE_ADMIN", "admin"),
    MANAGER("ROLE_MANAGER", "manager");

    private final String key;

    private final String role;

    public static String getHighestRole(Collection<? extends GrantedAuthority> authorities){

        return switch (authorities.size()) {
            case 3 -> ADMIN.role;
            case 2 -> MANAGER.role;
            default -> MEMBER.role;
        };
    }
}
