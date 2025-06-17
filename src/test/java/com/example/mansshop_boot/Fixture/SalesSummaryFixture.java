package com.example.mansshop_boot.Fixture;

import com.example.mansshop_boot.Fixture.domain.productSalesSummary.TestProductOptionSalesSummaryDTO;
import com.example.mansshop_boot.Fixture.domain.productSalesSummary.TestProductSalesSummaryDTO;
import com.example.mansshop_boot.Fixture.domain.productSalesSummary.TestSalesSummaryDTO;
import com.example.mansshop_boot.domain.dto.admin.business.AdminBestSalesProductDTO;
import com.example.mansshop_boot.domain.entity.PeriodSalesSummary;
import com.example.mansshop_boot.domain.entity.Product;
import com.example.mansshop_boot.domain.entity.ProductOption;
import com.example.mansshop_boot.domain.entity.ProductSalesSummary;

import java.time.LocalDate;
import java.util.*;

public class SalesSummaryFixture {

    public static List<ProductSalesSummary> createProductSalesSummary(List<Product> products) {
        LocalDate date = LocalDate.now().minusMonths(24);
        List<ProductSalesSummary> result = new ArrayList<>();
        for(int i = 0; i < 24; i++) {

            for(Product product : products) {

                for(ProductOption option : product.getProductOptions()) {

                    long orderQuantity = randomInt(99, 1);
                    long salesQuantity = randomInt((int) orderQuantity, 1);
                    long sales = product.getProductPrice() * salesQuantity;

                    result.add(
                            ProductSalesSummary.builder()
                                    .periodMonth(date)
                                    .classification(product.getClassification())
                                    .product(product)
                                    .productOption(option)
                                    .sales(sales)
                                    .salesQuantity(salesQuantity)
                                    .orderQuantity(orderQuantity)
                                    .build()
                    );
                }
            }
            date = date.plusMonths(1);
        }

        return result;
    }

    public static List<PeriodSalesSummary> createPeriodSalesSummary() {
        LocalDate date = LocalDate.of(2023, 1, 1);
        List<PeriodSalesSummary> result = new ArrayList<>();
        for(int i = 0; i < 730; i++) {
            long sales = randomInt(10000000, 1000000);
            long salesQuantity = randomInt(50000, 50000);
            long orderQuantity = randomInt(1000, 1000);
            long totalDeliveryFee = i % 3 == 0 ? 3500 * i : 0;
            long card = randomInt(100000000, 100000000);
            long cash = randomInt(100000000, 100000000);

            result.add(
                    PeriodSalesSummary.builder()
                            .period(date)
                            .sales(sales)
                            .salesQuantity(salesQuantity)
                            .orderQuantity(orderQuantity)
                            .totalDeliveryFee(totalDeliveryFee)
                            .cashTotal(cash)
                            .cardTotal(card)
                            .build()
            );

            date = date.plusDays(1);
        }

        return result;
    }


    private static int randomInt(int a, int b) {
        Random ran = new Random();

        return ran.nextInt(a) + b;
    }

    public static List<AdminBestSalesProductDTO> createBest5SalesDTO(List<ProductSalesSummary> list, LocalDate date) {
        Map<String, TestSalesSummaryDTO> salesMap = new HashMap<>();

        for(ProductSalesSummary summary : list) {
            LocalDate summaryDate = summary.getPeriodMonth();
            if(summaryDate.getYear() == date.getYear() && summaryDate.getMonthValue() == date.getMonthValue()) {
                String productName = summary.getProduct().getProductName();
                TestSalesSummaryDTO value = salesMap.getOrDefault(productName, new TestSalesSummaryDTO());
                value.addFieldsValue(summary.getSales(), summary.getSalesQuantity(), summary.getOrderQuantity());

                salesMap.put(productName, value);
            }
        }

        return salesMap.entrySet()
                        .stream()
                        .map(v ->
                                new AdminBestSalesProductDTO(
                                        v.getKey(),
                                        v.getValue().getSalesQuantity(),
                                        v.getValue().getSales()
                                )
                        )
                        .sorted(Comparator.comparing(AdminBestSalesProductDTO::productPeriodSalesQuantity).reversed())
                        .limit(5)
                        .toList();
    }

    public static Map<String, TestSalesSummaryDTO> createSalesDataListByClassifications(List<ProductSalesSummary> list, LocalDate date) {
        Map<String, TestSalesSummaryDTO> salesMap = new HashMap<>();

        for(ProductSalesSummary summary : list) {
            LocalDate summaryDate = summary.getPeriodMonth();
            if(summaryDate.getYear() == date.getYear() && summaryDate.getMonthValue() == date.getMonthValue()) {
                String classification = summary.getClassification().getId();
                TestSalesSummaryDTO value = salesMap.getOrDefault(classification, new TestSalesSummaryDTO());
                value.addFieldsValue(summary.getSales(), summary.getSalesQuantity(), summary.getOrderQuantity());

                salesMap.put(classification, value);
            }
        }

        return salesMap;
    }

    public static Map<String, TestSalesSummaryDTO> createSalesDataListByClassificationAndProduct(List<ProductSalesSummary> list,
                                                                                                 LocalDate start,
                                                                                                 LocalDate end,
                                                                                                 String classification) {
        Map<String, TestSalesSummaryDTO> salesMap = new HashMap<>();

        List<ProductSalesSummary> periodData = list.stream()
                .filter(v ->
                        v.getPeriodMonth().getYear() == start.getYear() && v.getPeriodMonth().getMonthValue() == start.getMonthValue()
                )
                .toList();

        for(ProductSalesSummary summary : periodData) {
            if(summary.getClassification().getId().equals(classification)) {
                String key = summary.getProduct().getProductName() + "_" +
                                summary.getProductOption().getSize() + "_" +
                                summary.getProductOption().getColor();

                TestSalesSummaryDTO value = salesMap.getOrDefault(key, new TestSalesSummaryDTO());
                value.addFieldsValue(summary.getSales(), summary.getSalesQuantity(), summary.getOrderQuantity());

                salesMap.put(key, value);
            }
        }

        return salesMap;
    }

    public static Map<String, TestProductSalesSummaryDTO> createSalesAllProductSalesSummary(List<ProductSalesSummary> list) {
        Map<String, TestProductSalesSummaryDTO> salesMap = new HashMap<>();

        for(ProductSalesSummary summary : list) {
            String key = summary.getProduct().getId();

            TestProductSalesSummaryDTO value = salesMap.getOrDefault(key, null);

            if(value == null)
                value = new TestProductSalesSummaryDTO(summary.getClassification().getId(),
                                                        summary.getProduct().getProductName(),
                                                        summary.getSales(),
                                                        summary.getProduct().getProductSalesQuantity()
                                                );
            else
                value.addSalesValue(summary.getSales());

            salesMap.put(key, value);
        }

        return salesMap;
    }

    public static Map<Integer, TestSalesSummaryDTO> createProductPeriodMonthSalesSummary(List<ProductSalesSummary> list,
                                                                                         int year,
                                                                                         String productId) {
        Map<Integer, TestSalesSummaryDTO> salesMap = new HashMap<>();

        for(ProductSalesSummary summary : list) {
            if(summary.getPeriodMonth().getYear() == year && summary.getProduct().getId().equals(productId)) {
                int key = summary.getPeriodMonth().getMonthValue();
                TestSalesSummaryDTO value = salesMap.getOrDefault(key, new TestSalesSummaryDTO());

                value.setSalesQuantity(summary.getProduct().getProductSalesQuantity());
                value.addFieldsValue(summary.getSales(), 0, summary.getOrderQuantity());

                salesMap.put(key, value);
            }
        }

        return salesMap;
    }

    public static Map<Long, TestProductOptionSalesSummaryDTO> createProductOptionPeriodYearSalesSummary(List<ProductSalesSummary> list,
                                                                                                        int year,
                                                                                                        String productId) {
        Map<Long, TestProductOptionSalesSummaryDTO> salesMap = new HashMap<>();

        for(ProductSalesSummary summary : list) {
            if(summary.getPeriodMonth().getYear() == year && summary.getProduct().getId().equals(productId)) {
                long key = summary.getProductOption().getId();
                TestProductOptionSalesSummaryDTO value = salesMap.getOrDefault(key, null);

                if(value == null) {
                    value = new TestProductOptionSalesSummaryDTO(summary.getProductOption().getSize(),
                                                                summary.getProductOption().getColor(),
                                                                summary.getSales(),
                                                                summary.getSalesQuantity()
                                                        );
                }else
                    value.addSalesFieldsValue(summary.getSales(), summary.getSalesQuantity());

                salesMap.put(key, value);
            }
        }

        return salesMap;
    }
}
