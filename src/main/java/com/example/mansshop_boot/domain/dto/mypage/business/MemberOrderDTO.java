package com.example.mansshop_boot.domain.dto.mypage.business;

import lombok.Builder;

public record MemberOrderDTO(
        String userId,
        String recipient,
        String phone
) {

    @Builder
    public MemberOrderDTO(String userId, String recipient, String phone) {
        String phoneRegEx = "(\\d{3})(\\d{3,4})(\\d{4})";
        this.userId = userId;
        this.recipient = recipient;
        this.phone = phone == null ? null : phone.replaceAll(phoneRegEx, "$1-$2-$3");
    }
}
