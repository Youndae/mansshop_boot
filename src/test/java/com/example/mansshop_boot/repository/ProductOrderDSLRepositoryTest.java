package com.example.mansshop_boot.repository;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductOrderDSLRepositoryTest {

    /*@Autowired
    private ProductOrderRepository productOrderRepository;

    @Test
    @DisplayName("year 값을 받아 해당 연도의 월별 매출 조회 테스트")
    void adminPeriodSalesListText() {
        int year = 2024;

        List<AdminPeriodSalesListDTO> list = productOrderRepository.findPeriodList(year);

        list.forEach(System.out::println);
    }

    @Test
    @DisplayName("해당 월의 통계 조회")
    void getPeriodStatistics() {
        LocalDateTime startDate = LocalDateTime.of(2024, 6, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 6, 30, 0, 0);

        AdminPeriodSalesStatisticsDTO dto = productOrderRepository.findPeriodStatistics(startDate, endDate);

        System.out.println("dto : " + dto);
    }

    @Test
    @DisplayName("해당 월의 일 매출 리스트")
    void getPeriodDailyList() {

        LocalDateTime startDate = LocalDateTime.of(2024, 6, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 6, 30, 0, 0);

        List<AdminPeriodSalesListDTO> dto = productOrderRepository.findPeriodDailyList(startDate, endDate);

        dto.forEach(System.out::println);
    }

    @Test
    @DisplayName("상품 매출 조회")
    void getProductSalesList() {
        AdminPageDTO pageDTO = new AdminPageDTO("null", 1);

        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                , pageDTO.amount()
                , Sort.by("classificationStep").ascending());

        Page<AdminProductSalesListDTO> response = productOrderRepository.getProductSalesList(pageDTO, pageable);

        response.getContent().forEach(System.out::println);
    }*/
}