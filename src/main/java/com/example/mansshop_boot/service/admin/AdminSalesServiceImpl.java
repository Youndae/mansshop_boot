package com.example.mansshop_boot.service.admin;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomNotFoundException;
import com.example.mansshop_boot.domain.dto.admin.business.*;
import com.example.mansshop_boot.domain.dto.admin.out.*;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.PagingMappingDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.entity.ProductOrder;
import com.example.mansshop_boot.domain.enumeration.PageAmount;
import com.example.mansshop_boot.repository.periodSales.PeriodSalesSummaryRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderDetailRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderRepository;
import com.example.mansshop_boot.repository.productSales.ProductSalesSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminSalesServiceImpl implements AdminSalesService {

    private final PeriodSalesSummaryRepository periodSalesSummaryRepository;

    private final ProductSalesSummaryRepository productSalesSummaryRepository;

    private final ProductOrderRepository productOrderRepository;

    private final ProductOrderDetailRepository productOrderDetailRepository;

    /**
     *
     * @param term
     *
     * 기간별 매출 조회.
     * term으로는 YYYY로 연도가 전달.
     * 해당 연도에 대해 매출, 판매량, 주문량, 월별 매출 리스트을 조회.
     *
     * 월 매출 리스트로는 월, 매출, 판매량, 주문량으로 조회.
     * 데이터가 존재하지 않는 월에 대한 데이터를 처리하기 위해 반복문으로 date 필드를 체크.
     * 데이터가 없다면 date를 제외한 나머지 필드를 0으로 생성해서 저장.
     */
    @Override
    public AdminPeriodSalesResponseDTO getPeriodSales(int term) {
        List<AdminPeriodSalesListDTO> selectList = periodSalesSummaryRepository.findPeriodList(term);
        Map<Integer, AdminPeriodSalesListDTO> map = selectList.stream()
                .collect(
                        Collectors.toMap(AdminPeriodSalesListDTO::date, dto -> dto)
                );

        List<AdminPeriodSalesListDTO> contentList = new ArrayList<>();
        long yearSales = 0;
        long yearSalesQuantity = 0;
        long yearOrderQuantity = 0;

        for(int i = 1; i <= 12; i++) {
            AdminPeriodSalesListDTO content = map.getOrDefault(i, new AdminPeriodSalesListDTO(i));
            yearSales += content.sales();
            yearSalesQuantity += content.salesQuantity();
            yearOrderQuantity += content.orderQuantity();

            contentList.add(content);
        }

        return new AdminPeriodSalesResponseDTO(
                contentList
                , yearSales
                , yearSalesQuantity
                , yearOrderQuantity
        );
    }

    /**
     *
     * @param term
     *
     * 기간별 매출 상세 조회
     * 연월 기준으로 조회.
     * term값으로는 YYYY-MM 구조로 받는다.
     * 해당 월의 매출, 판매량, 주문량, 전년 동월과의 매출 비교, 전년 동월의 매출, 판매량, 주문량
     * 해당 월의 베스트 매출 상품 5개, 상품 분류별 월 매출, 일별 매출을 조회한다.
     */
    @Override
    public AdminPeriodMonthDetailResponseDTO getPeriodSalesDetail(String term) {
        String[] termSplit = term.split("-");
        int year = Integer.parseInt(termSplit[0]);
        int month = Integer.parseInt(termSplit[1]);

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);
        int lastDay = YearMonth.from(startDate).lengthOfMonth();

        AdminPeriodSalesStatisticsDTO monthStatistics = periodSalesSummaryRepository.findPeriodStatistics(startDate, endDate);

        if(monthStatistics == null)
            return new AdminPeriodMonthDetailResponseDTO(
                    AdminPeriodSalesStatisticsDTO.emptyDTO(),
                    getLastYearStatistics(startDate, endDate),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList()
            );

        List<AdminBestSalesProductDTO> bestProductList = productSalesSummaryRepository.findPeriodBestProductOrder(startDate, endDate);
        List<AdminPeriodSalesListDTO> dailySalesResponseDTO = getPeriodSalesList(lastDay, startDate, endDate);
        List<AdminPeriodClassificationDTO> classificationResponseDTO = productSalesSummaryRepository.findPeriodClassification(startDate, endDate);
        AdminPeriodSalesStatisticsDTO lastYearStatistics = getLastYearStatistics(startDate, endDate);

        return new AdminPeriodMonthDetailResponseDTO(
                monthStatistics,
                lastYearStatistics,
                bestProductList,
                classificationResponseDTO,
                dailySalesResponseDTO
        );
    }

    private AdminPeriodSalesStatisticsDTO getLastYearStatistics(LocalDate startDate, LocalDate endDate) {
        startDate = startDate.minusYears(1);
        endDate = endDate.minusYears(1);

        AdminPeriodSalesStatisticsDTO lastYearStatistics = periodSalesSummaryRepository.findPeriodStatistics(startDate, endDate);

        if(lastYearStatistics == null)
            return AdminPeriodSalesStatisticsDTO.emptyDTO();

        return lastYearStatistics;
    }

    private List<AdminPeriodSalesListDTO> getPeriodSalesList(int lastDay, LocalDate startDate, LocalDate endDate) {
        List<AdminPeriodSalesListDTO> dailySalesList = periodSalesSummaryRepository.findPeriodDailyList(startDate, endDate);
        Map<Integer, AdminPeriodSalesListDTO> dailyMap = dailySalesList.stream()
                .collect(
                        Collectors.toMap(AdminPeriodSalesListDTO::date, dto -> dto)
                );

        return IntStream.rangeClosed(1, lastDay)
                .mapToObj(v ->
                        dailyMap.getOrDefault(v, new AdminPeriodSalesListDTO(v))
                ).toList();
    }

    /**
     *
     * @param term
     * @param classification
     *
     * 상품 분류의 월 매출 내역 조회.
     * term은 YYYY-MM 구조.
     * 상품명, 해당 월 매출, 판매량, 상품별 매출[] 형태로 반환.
     * 상품별 매출 데이터의 경우 상품명, 사이즈, 컬러, 매출, 판매량이 반환된다.
     */
    @Override
    public AdminClassificationSalesResponseDTO getSalesByClassification(String term, String classification) {
        int[] termSplit = Arrays.stream(term.split("-"))
                .mapToInt(Integer::parseInt)
                .toArray();

        LocalDate startDate = LocalDate.of(termSplit[0], termSplit[1], 1);
        LocalDate endDate = startDate.plusMonths(1);

        AdminClassificationSalesDTO classificationSalesDTO = productSalesSummaryRepository.findPeriodClassificationSales(startDate, endDate, classification);
        System.out.println("classification: " + classificationSalesDTO);
        if(classificationSalesDTO == null)
            return new AdminClassificationSalesResponseDTO(
                    classification,
                    AdminClassificationSalesDTO.emptyDTO(),
                    Collections.emptyList()
            );

        List<AdminClassificationSalesProductListDTO> productList = productSalesSummaryRepository.findPeriodClassificationProductSales(startDate, endDate, classification);

        return new AdminClassificationSalesResponseDTO(classification, classificationSalesDTO, productList);
    }

    /**
     *
     * @param term
     *
     * 일매출 정보 조회.
     * term은 YYYY-MM-DD 구조.
     * 해당 일의 분류별 매출, 판매량을 조회하고 그 날의 매출, 판매량, 주문량을 조회한다.
     */
    @Override
    public AdminPeriodSalesResponseDTO getSalesByDay(String term) {
        int[] termSplit = Arrays.stream(term.split("-"))
                .mapToInt(Integer::parseInt)
                .toArray();
        LocalDate startDate = LocalDate.of(termSplit[0], termSplit[1], termSplit[2]);
        LocalDate endDate = startDate.plusDays(1);

        AdminClassificationSalesDTO salesDTO = periodSalesSummaryRepository.findDailySales(startDate);

        if(salesDTO == null)
            return new AdminPeriodSalesResponseDTO(
                    Collections.emptyList(),
                    0,
                    0,
                    0
            );


        List<AdminPeriodClassificationDTO> classificationList = productSalesSummaryRepository.findPeriodClassification(startDate, endDate);

        return new AdminPeriodSalesResponseDTO(
                classificationList
                , salesDTO.sales()
                , salesDTO.salesQuantity()
                , salesDTO.orderQuantity()
        );
    }

    /**
     *
     * @param term
     * @param page
     *
     * 해당 일자의 주문 내역 조회
     * term은 YYYY-MM-DD 구조.
     * 해당 일자의 모든 주문 내역과 상세 내역을 조회.
     */
    @Override
    public PagingListDTO<AdminDailySalesResponseDTO> getOrderListByDay(String term, int page) {
        int[] termSplit = Arrays.stream(term.split("-"))
                .mapToInt(Integer::parseInt)
                .toArray();
        LocalDate start = LocalDate.of(termSplit[0], termSplit[1], termSplit[2]);
        LocalDateTime startDate = LocalDateTime.of(start, LocalTime.MIN);
        LocalDateTime endDate = LocalDateTime.of(start, LocalTime.MAX);

        Pageable pageable = PageRequest.of(page - 1
                , PageAmount.ADMIN_DAILY_ORDER_AMOUNT.getAmount()
                , Sort.by("createdAt").descending());

        Page<ProductOrder> orderList = productOrderRepository.findAllByDay(startDate, endDate, pageable);
        List<Long> orderIdList = orderList.stream().map(ProductOrder::getId).toList();
        List<AdminOrderDetailListDTO> orderDetailList = productOrderDetailRepository.findByOrderIds(orderIdList);

        List<AdminDailySalesResponseDTO> content = orderList.getContent()
                .stream()
                .map(v -> {
                    List<AdminDailySalesDetailDTO> detailContent = orderDetailList.stream()
                            .filter(orderDetail -> v.getId() == orderDetail.orderId())
                            .map(AdminDailySalesDetailDTO::new)
                            .toList();

                    return new AdminDailySalesResponseDTO(v, detailContent);
                })
                .toList();

        PagingMappingDTO pagingMappingDTO = PagingMappingDTO.builder()
                .totalElements(orderList.getTotalElements())
                .number(orderList.getNumber())
                .empty(orderList.isEmpty())
                .totalPages(orderList.getTotalPages())
                .build();

        return new PagingListDTO<>(content, pagingMappingDTO);
    }

    /**
     *
     * @param pageDTO
     *
     * 상품별 매출 조회.
     * 상품 분류의 순서를 기준으로 정렬.
     */
    @Override
    public Page<AdminProductSalesListDTO> getProductSalesList(AdminPageDTO pageDTO) {
        Pageable pageable = PageRequest.of(pageDTO.page() - 1,
                pageDTO.amount(),
                Sort.by("classificationStep").ascending());

        return productSalesSummaryRepository.findProductSalesList(pageDTO, pageable);
    }

    /**
     *
     * @param productId
     *
     * 상품의 매출 조회.
     *
     * 상품명, 총 매출, 총 판매량, 올해 매출, 올해 판매량, 전년과 비교, 전년 매출, 전년 판매량,
     * 월별 매출[월, 매출, 판매량, 주문량], 옵션별 매출[옵션 아이디, 사이즈, 컬러, 매출, 판매량],
     * 옵션별 올해 매출[옵션 아이디, 사이즈, 컬러, 매출, 판매량], 옵션별 전년도 매출[옵션 아이디, 사이즈, 컬러, 매출, 판매량]
     */
    @Override
    public AdminProductSalesDetailDTO getProductSalesDetail(String productId) {
        LocalDate date = LocalDate.now();
        int year = date.getYear();

        AdminProductSalesDTO totalSalesDTO = productSalesSummaryRepository.getProductSales(productId);

        if(totalSalesDTO == null)
            throw new CustomNotFoundException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());

        AdminSalesDTO yearSalesDTO = productSalesSummaryRepository.getProductPeriodSales(year, productId);
        AdminSalesDTO lastYearSalesDTO = productSalesSummaryRepository.getProductPeriodSales(year - 1, productId);
        List<AdminPeriodSalesListDTO> monthSalesDTO = productSalesSummaryRepository.getProductMonthPeriodSales(year, productId);
        List<AdminProductSalesOptionDTO> optionTotalSalesList = productSalesSummaryRepository.getProductOptionSales(0, productId);
        List<AdminProductSalesOptionDTO> optionYearSalesList = productSalesSummaryRepository.getProductOptionSales(year, productId);
        List<AdminProductSalesOptionDTO> optionLastYearSalesList = productSalesSummaryRepository.getProductOptionSales(year - 1, productId);
        Map<Integer, AdminPeriodSalesListDTO> map = monthSalesDTO.stream()
                .collect(
                        Collectors.toMap(AdminPeriodSalesListDTO::date, dto -> dto)
                );
        List<AdminPeriodSalesListDTO> monthSalesMappingDTO = new ArrayList<>();
        for(int i = 1; i <= 12; i++) {
            AdminPeriodSalesListDTO content = map.getOrDefault(i, new AdminPeriodSalesListDTO(i));
            monthSalesMappingDTO.add(content);
        }

        return new AdminProductSalesDetailDTO(
                totalSalesDTO
                , yearSalesDTO
                , lastYearSalesDTO
                , monthSalesMappingDTO
                , optionTotalSalesList
                , optionYearSalesList
                , optionLastYearSalesList
        );
    }
}
