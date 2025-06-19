package com.example.mansshop_boot.domain.dto.mypage.business;

/**
 *
 * @param pageNum
 * @param amount
 *
 * MyPage Review, QnA PageDTO
 */
public record MyPagePageDTO(
        int pageNum,
        int amount
) {
    public MyPagePageDTO(int pageNum) {
        this(pageNum, 20);
    }
}
