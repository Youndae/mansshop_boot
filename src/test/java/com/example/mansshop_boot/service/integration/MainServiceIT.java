package com.example.mansshop_boot.service.integration;

import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.main.out.MainListResponseDTO;
import com.example.mansshop_boot.domain.dto.pageable.MainPageDTO;
import com.example.mansshop_boot.service.MainService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
public class MainServiceIT {

    @Autowired
    private MainService mainService;

    @Test
    @DisplayName(value = "메인 베스트 상품 리스트 조회")
    void getBestList() {
        MainPageDTO pageDTO = new MainPageDTO("BEST");
        List<MainListResponseDTO> result = mainService.getBestAndNewList(pageDTO, null);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(pageDTO.mainProductAmount(), result.size());
    }

    @Test
    @DisplayName(value = "메인 새로운 상품 리스트")
    void getNewList() {
        MainPageDTO pageDTO = new MainPageDTO("NEW");
        List<MainListResponseDTO> result = mainService.getBestAndNewList(pageDTO, null);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(pageDTO.mainProductAmount(), result.size());
    }

    @Test
    @DisplayName(value = "메인 선택한 상품 분류(OUTER) 리스트")
    void getOUTERList() {
        MainPageDTO pageDTO = new MainPageDTO(1, null, "OUTER");
        List<MainListResponseDTO> result = mainService.getBestAndNewList(pageDTO, null);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(pageDTO.mainProductAmount(), result.size());
    }
}
