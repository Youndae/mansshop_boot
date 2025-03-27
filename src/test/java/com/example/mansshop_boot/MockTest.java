package com.example.mansshop_boot;

import com.example.mansshop_boot.domain.dto.admin.out.AdminQnAListResponseDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.repository.productQnA.ProductQnARepository;
import com.example.mansshop_boot.service.AdminService;
import com.example.mansshop_boot.service.AdminServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
@ComponentScan(basePackages = "com.example")
public class MockTest {

    @InjectMocks
    private AdminServiceImpl adminService;

    @Mock
    private ProductQnARepository productQnARepository;

    /*@Test
    @DisplayName(value = "getQnAList UnitTest")
    void getQnAMock() {
        List<AdminQnAListResponseDTO> responseMock = new ArrayList<>();
        AdminQnAListResponseDTO dto1 = new AdminQnAListResponseDTO(1L, "OUTER", "testMockProduct", "coco", LocalDate.now(), true);
        AdminQnAListResponseDTO dto2 = new AdminQnAListResponseDTO(2L, "OUTER", "testMockProduct2", "coco2", LocalDate.now(), false);

        responseMock.add(dto1);
        responseMock.add(dto2);

        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, "all", 1);

        when(productQnARepository.findAllByAdminProductQnA(pageDTO))
                .thenReturn(responseMock);
        when(productQnARepository.findAllByAdminProductQnACount(pageDTO))
                .thenReturn(100L);

        PagingListDTO<AdminQnAListResponseDTO> result = adminService.getProductQnAList(pageDTO);

        result.content().forEach(System.out::println);

        Assertions.assertEquals(100L, result.pagingData().getTotalElements());
    }*/
}
