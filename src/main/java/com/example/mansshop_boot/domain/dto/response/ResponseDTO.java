package com.example.mansshop_boot.domain.dto.response;

import com.example.mansshop_boot.domain.dto.member.UserStatusDTO;

public record ResponseDTO<T>(
        T content
        , UserStatusDTO userStatus
) {
}
