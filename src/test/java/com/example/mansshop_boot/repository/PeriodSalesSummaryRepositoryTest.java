package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.Fixture.SalesSummaryFixture;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.admin.business.AdminClassificationSalesDTO;
import com.example.mansshop_boot.domain.dto.admin.business.AdminPeriodSalesListDTO;
import com.example.mansshop_boot.domain.dto.admin.business.AdminPeriodSalesStatisticsDTO;
import com.example.mansshop_boot.domain.entity.PeriodSalesSummary;
import com.example.mansshop_boot.repository.periodSales.PeriodSalesSummaryRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = MansShopBootApplication.class)
@ActiveProfiles("test")
public class PeriodSalesSummaryRepositoryTest {

    @Autowired
    private PeriodSalesSummaryRepository periodSalesSummaryRepository;

    private List<PeriodSalesSummary> summaryListBy2023;

    @BeforeAll
    void init() {
        List<PeriodSalesSummary> fixture = SalesSummaryFixture.createPeriodSalesSummary();

        periodSalesSummaryRepository.saveAll(fixture);

        summaryListBy2023 = fixture.stream()
                                .filter(v -> v.getPeriod().getYear() == 2023)
                                .toList();
    }

    @Test
    @DisplayName(value = "해당 연도의 월별 매출 조회")
    void findPeriodList() {
        int year = 2023;
        List<AdminPeriodSalesListDTO> data = new ArrayList<>();

        for(int i = 1; i <= 12; i++) {
            long sales = 0L;
            long salesQuantity = 0L;
            long orderQuantity = 0L;

            for(PeriodSalesSummary v : summaryListBy2023) {
                if(v.getPeriod().getMonthValue() == i){
                    sales += v.getSales();
                    salesQuantity += v.getSalesQuantity();
                    orderQuantity += v.getOrderQuantity();
                }
            }

            data.add(new AdminPeriodSalesListDTO(i, sales, salesQuantity, orderQuantity));
        }

        List<AdminPeriodSalesListDTO> result = periodSalesSummaryRepository.findPeriodList(year);

        assertNotNull(result);

        for(int i = 0; i < result.size(); i++) {
            AdminPeriodSalesListDTO dataPeriod = data.get(i);
            AdminPeriodSalesListDTO resultPeriod = result.get(i);

            assertEquals(dataPeriod.date(), resultPeriod.date());
            assertEquals(dataPeriod.sales(), resultPeriod.sales());
            assertEquals(dataPeriod.salesQuantity(), resultPeriod.salesQuantity());
            assertEquals(dataPeriod.orderQuantity(), resultPeriod.orderQuantity());
        }
    }

    @Test
    @DisplayName(value = "기간의 매출 조회. 월 매출을 조회")
    void findPeriodStatistics() {
        int year = 2023;
        int month = 1;
        long sales = 0L;
        long salesQuantity = 0L;
        long orderQuantity = 0L;
        long deliveryFee = 0L;
        long cashTotal = 0L;
        long cardTotal = 0L;
        for(PeriodSalesSummary v : summaryListBy2023) {
            if(v.getPeriod().getMonthValue() == month){
                sales += v.getSales();
                salesQuantity += v.getSalesQuantity();
                orderQuantity += v.getOrderQuantity();
                deliveryFee += v.getTotalDeliveryFee();
                cashTotal += v.getCashTotal();
                cardTotal += v.getCardTotal();
            }
        }

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);

        AdminPeriodSalesStatisticsDTO result = periodSalesSummaryRepository.findPeriodStatistics(startDate, endDate);

        assertNotNull(result);
        assertEquals(sales, result.monthSales());
        assertEquals(salesQuantity, result.monthSalesQuantity());
        assertEquals(orderQuantity, result.monthOrderQuantity());
        assertEquals(deliveryFee, result.monthDeliveryFee());
        assertEquals(cashTotal, result.cashTotalPrice());
        assertEquals(cardTotal, result.cardTotalPrice());
    }

    @Test
    @DisplayName(value = "기간의 일별 매출 조회")
    void findPeriodDailyList() {
        int year = 2023;
        int month = 1;
        List<AdminPeriodSalesListDTO> dataList = new ArrayList<>();

        for(int i = 1; i <= 31; i++) {

            for(PeriodSalesSummary val : summaryListBy2023) {
                if(val.getPeriod().getMonthValue() == month && val.getPeriod().getDayOfMonth() == i){
                    dataList.add(new AdminPeriodSalesListDTO(i, val.getSales(), val.getSalesQuantity(), val.getOrderQuantity()));
                }
            }
        }

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);

        List<AdminPeriodSalesListDTO> result = periodSalesSummaryRepository.findPeriodDailyList(startDate, endDate);
        assertNotNull(result);
        assertEquals(dataList.size(), result.size());

        for(int i = 0; i < 31; i++) {
            AdminPeriodSalesListDTO data = dataList.get(i);
            AdminPeriodSalesListDTO resultData = result.get(i);

            assertEquals(data.date(), resultData.date());
            assertEquals(data.sales(), resultData.sales());
            assertEquals(data.salesQuantity(), resultData.salesQuantity());
            assertEquals(data.orderQuantity(), resultData.orderQuantity());
        }
    }

    @Test
    @DisplayName(value = "해당 일자의 매출 조회")
    void findDailySales() {
        int year = 2023;
        int month = 1;
        int day = 1;

        AdminClassificationSalesDTO data = null;

        for(PeriodSalesSummary val : summaryListBy2023) {
            if(val.getPeriod().getMonthValue() == month && val.getPeriod().getDayOfMonth() == day){
                data = new AdminClassificationSalesDTO(val.getSales(), val.getSalesQuantity(), val.getOrderQuantity());
                break;
            }
        }

        AdminClassificationSalesDTO result = periodSalesSummaryRepository.findDailySales(LocalDate.of(year, month, day));

        assertNotNull(result);
        assertEquals(data.sales(), result.sales());
        assertEquals(data.salesQuantity(), result.salesQuantity());
        assertEquals(data.orderQuantity(), result.orderQuantity());
    }

    @Test
    void findByPeriod() {
        PeriodSalesSummary data = summaryListBy2023.get(0);
        PeriodSalesSummary result = periodSalesSummaryRepository.findByPeriod(data.getPeriod());

        assertNotNull(result);
        assertEquals(data.getPeriod(), result.getPeriod());
        assertEquals(data.getSales(), result.getSales());
        assertEquals(data.getSalesQuantity(), result.getSalesQuantity());
        assertEquals(data.getOrderQuantity(), result.getOrderQuantity());
    }
}
