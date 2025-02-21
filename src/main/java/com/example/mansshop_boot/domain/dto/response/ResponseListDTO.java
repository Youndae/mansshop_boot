package com.example.mansshop_boot.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record ResponseListDTO<T>(
        @Schema(description = "List Content")
        List<T> content,
        @Schema(description = "user Login Status Info", implementation = UserStatusDTO.class)
        UserStatusDTO userStatus
) {
}
