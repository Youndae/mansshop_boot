package com.example.mansshop_boot.domain.dto.mypage;

import com.example.mansshop_boot.domain.entity.Member;
import lombok.Builder;

@Builder
public record MyPageInfoDTO(
        String nickname
        , String phone
        , String mailPrefix
        , String mailSuffix
        , String mailType
) {

    public MyPageInfoDTO(Member member, String[] splitMail, String mailType) {
        this(
                member.getNickname()
                , member.getPhone().replaceAll("-", "")
                , splitMail[0]
                , splitMail[1]
                , mailType
        );
    }
}
