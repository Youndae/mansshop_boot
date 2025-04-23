package com.example.mansshop_boot.service.unit.fixture;

import com.example.mansshop_boot.domain.entity.Member;

public class MemberUnitFixture {

    public static Member createMemberFixture() {
        return Member.builder()
                .userId("coco")
                .build();
    }
}
