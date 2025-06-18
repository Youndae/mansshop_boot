package com.example.mansshop_boot.Fixture;

import com.example.mansshop_boot.domain.dto.pageable.MainPageDTO;

public class PageDTOFixture {

    public static MainPageDTO createDefaultMainPageDTO(String classification) {
        return new MainPageDTO(classification);
    }

    public static MainPageDTO createMainPageDTO(int page, String keyword, String classification) {
        return new MainPageDTO(page, keyword, classification);
    }
}
