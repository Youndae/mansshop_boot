package com.example.mansshop_boot.Fixture;

import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;

public class AdminPageDTOFixture {

    public static AdminOrderPageDTO createDefaultAdminOrderPageDTO() {
        return new AdminOrderPageDTO(null, null, 1);
    }

    public static AdminOrderPageDTO createAllListAdminOrderPageDTO() {
        return new AdminOrderPageDTO(null, "all", 1);
    }

    public static AdminOrderPageDTO createNewListAdminOrderPageDTO() {
        return new AdminOrderPageDTO(null, "new", 1);
    }

    public static AdminOrderPageDTO createSearchAdminOrderPageDTO(String keyword, String searchType, int page) {
        return new AdminOrderPageDTO(keyword, searchType, page);
    }

    public static AdminPageDTO createDefaultAdminPageDTO() {
        return new AdminPageDTO(null, 1);
    }

    public static AdminPageDTO createSearchAdminPageDTO(String keyword, int page) {
        return new AdminPageDTO(keyword, page);
    }
}
