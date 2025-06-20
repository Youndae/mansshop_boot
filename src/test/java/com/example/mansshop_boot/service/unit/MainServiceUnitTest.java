package com.example.mansshop_boot.service.unit;

import com.example.mansshop_boot.domain.dto.main.business.MainListDTO;
import com.example.mansshop_boot.domain.dto.main.out.MainListResponseDTO;
import com.example.mansshop_boot.domain.dto.pageable.MainPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.service.MainServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MainServiceUnitTest {

    @InjectMocks
    private MainServiceImpl mainService;

    @Mock
    private ProductRepository productRepository;

    @Test
    @DisplayName(value = "베스트 상품 리스트 조회")
    void getBestProductList() {
        MainPageDTO pageDTO = MainPageDTO.builder()
                .pageNum(1)
                .classification("BEST")
                .keyword(null)
                .build();
        List<MainListDTO> resultList = List.of(
                new MainListDTO(
                        "testProductId",
                        "testProductName",
                        "testThumbnail",
                        10000,
                        10,
                        100
                )
        );

        when(productRepository.findListDefault(pageDTO)).thenReturn(resultList);

        List<MainListResponseDTO> result = assertDoesNotThrow(() -> mainService.getBestAndNewList(pageDTO));

        assertEquals(resultList.size(), result.size());
    }

    @Test
    @DisplayName(value = "새로운 상품 리스트 조회")
    void getNewProductList() {
        MainPageDTO pageDTO = MainPageDTO.builder()
                .pageNum(1)
                .classification("NEW")
                .keyword(null)
                .build();
        List<MainListDTO> resultList = List.of(
                new MainListDTO(
                        "testProductId",
                        "testProductName",
                        "testThumbnail",
                        10000,
                        10,
                        100
                )
        );

        when(productRepository.findListDefault(pageDTO)).thenReturn(resultList);

        List<MainListResponseDTO> result = assertDoesNotThrow(() -> mainService.getBestAndNewList(pageDTO));

        assertEquals(resultList.size(), result.size());
    }

    @Test
    @DisplayName(value = "베스트 또는 새로운 상품 리스트 조회. 상품 데이터가 없는 경우")
    void getBestAndNewListEmpty() {
        MainPageDTO pageDTO = MainPageDTO.builder()
                .pageNum(1)
                .classification("NEW")
                .keyword(null)
                .build();
        List<MainListDTO> resultList = Collections.emptyList();

        when(productRepository.findListDefault(pageDTO)).thenReturn(resultList);

        List<MainListResponseDTO> result = assertDoesNotThrow(() -> mainService.getBestAndNewList(pageDTO));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName(value = "상품 분류에 따른 목록 조회")
    void getClassificationAndSearchList() {
        MainPageDTO pageDTO = MainPageDTO.builder()
                .pageNum(1)
                .classification("OUTER")
                .keyword(null)
                .build();
        List<MainListDTO> resultList = List.of(
                new MainListDTO(
                        "testProductId",
                        "testProductName",
                        "testThumbnail",
                        10000,
                        10,
                        100
                )
        );
        Pageable pageable =  PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.mainProductAmount()
                , Sort.by("createdAt").descending()
        );
        Page<MainListDTO> resultPages = new PageImpl<>(resultList);

        when(productRepository.findListPageable(pageDTO, pageable)).thenReturn(resultPages);

        PagingListDTO<MainListResponseDTO> result = assertDoesNotThrow(() ->mainService.getClassificationAndSearchList(pageDTO));

        assertNotNull(result);
        assertEquals(resultList.size(), result.pagingData().getTotalElements());
        assertFalse(result.pagingData().isEmpty());
        assertEquals(1, result.pagingData().getTotalPages());
        assertEquals(resultList.size(), result.content().size());
    }
}
