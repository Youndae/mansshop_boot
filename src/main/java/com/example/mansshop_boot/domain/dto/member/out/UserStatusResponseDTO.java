package com.example.mansshop_boot.domain.dto.member.out;

import com.example.mansshop_boot.auth.user.CustomUser;
import com.example.mansshop_boot.domain.enumeration.Role;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class UserStatusResponseDTO {

    private final String userId;

    private final String role;

    public UserStatusResponseDTO(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        this.userId = authentication.getName();
        this.role = Role.getHighestRole(authorities);
    }

    public UserStatusResponseDTO(CustomUser customUser) {
        Collection<? extends GrantedAuthority> authorities = customUser.getAuthorities();

        this.userId = customUser.getUserId();
        this.role = Role.getHighestRole(authorities);
    }
}
