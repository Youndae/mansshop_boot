package com.example.mansshop_boot.Fixture;

import com.example.mansshop_boot.domain.dto.mypage.business.MyPagePageDTO;
import com.example.mansshop_boot.domain.dto.pageable.*;

public class PageDTOFixture {

    public static MainPageDTO createDefaultMainPageDTO(String classification) {
        return new MainPageDTO(classification);
    }

    public static MainPageDTO createMainPageDTO(int page, String keyword, String classification) {
        return new MainPageDTO(page, keyword, classification);
    }

    public static OrderPageDTO createDefaultOrderPageDTO(String term) {
        return new OrderPageDTO(1, term);
    }

    public static LikePageDTO createDefaultLikePageDTO(int page) {
        return new LikePageDTO(page);
    }

    public static MyPagePageDTO createDefaultMyPagePageDTO(int page) {
        return new MyPagePageDTO(page);
    }

    public static ProductDetailPageDTO createDefaultProductDetailPageDTO(int page) {
        return new ProductDetailPageDTO(page);
    }

    public static AdminOrderPageDTO createDefaultAdminOrderPageDTO(int page) {
        return new AdminOrderPageDTO(null, null, page);
    }

    public static AdminOrderPageDTO createSearchAdminOrderPageDTO(String keyword, String searchType, int page) {
        return new AdminOrderPageDTO(keyword, searchType, page);
    }

    public static AdminPageDTO createDefaultAdminPageDTO(int page) {
        return new AdminPageDTO(null, page);
    }
}
