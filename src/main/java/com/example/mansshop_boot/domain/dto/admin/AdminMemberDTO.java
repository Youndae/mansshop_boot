package com.example.mansshop_boot.domain.dto.admin;

import java.time.LocalDate;

public record AdminMemberDTO(
        String userId
        , String userName
        , String nickname
        , String phone
        , String email
        , LocalDate birth
        , long point
        , LocalDate createdAt
) {

    public AdminMemberDTO(String userId, String userName, String nickname, String phone, String email, LocalDate birth, long point, LocalDate createdAt) {
        String phoneRegEx = "(\\d{3})(\\d{3,4})(\\d{4})";

        this.userId = userId;
        this.userName = userName;
        this.nickname = nickname;
        this.phone = phone == null ? null : phone.replaceAll(phoneRegEx, "$1-$2-$3");
        this.email = email;
        this.birth = birth;
        this.point = point;
        this.createdAt = createdAt;
    }
}
