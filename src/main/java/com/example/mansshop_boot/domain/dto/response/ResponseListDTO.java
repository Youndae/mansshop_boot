package com.example.mansshop_boot.domain.dto.response;

import com.example.mansshop_boot.domain.dto.member.UserStatusDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record ResponseListDTO<T>(
        List<T> content
        , UserStatusDTO userStatus
) {
}
