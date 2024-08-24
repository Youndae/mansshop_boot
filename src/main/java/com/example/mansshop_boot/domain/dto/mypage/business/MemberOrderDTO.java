package com.example.mansshop_boot.domain.dto.mypage.business;

import lombok.Builder;

public record MemberOrderDTO(
        String userId
        , String recipient
        , String phone
) {

    @Builder
    public MemberOrderDTO(String userId, String recipient, String phone) {
        this.userId = userId;
        this.recipient = recipient;
        this.phone = phone;
    }
}
