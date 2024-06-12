package com.example.mansshop_boot.domain.dto.mypage;

import com.example.mansshop_boot.domain.dto.member.UserStatusDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record QnAClassificationResponseDTO(
        List<QnAClassificationDTO> classificationList
        , UserStatusDTO userStatus
) {
}
