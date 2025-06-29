package com.example.mansshop_boot.Fixture;

import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.entity.ProductOption;
import com.example.mansshop_boot.domain.entity.ProductOrder;
import com.example.mansshop_boot.domain.entity.ProductOrderDetail;
import com.example.mansshop_boot.domain.enumeration.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProductOrderFixture {


    // 저장되지 않은 상태의 ProductOrder, ProductOrderDetail 데이터 생성
    public static List<ProductOrder> createDefaultProductOrder(List<Member> members, List<ProductOption> options) {
        List<ProductOrder> result = new ArrayList<>();
        for(Member m : members) {
            int optionCount = randomInt();
            int productCount = 0;
            int totalPrice = 0;
            int phoneSuffix = 2345;
            List<ProductOrderDetail> details = new ArrayList<>();
            for(int i = 0; i < optionCount; i++) {
                details.add(
                        ProductOrderDetail.builder()
                                .productOption(options.get(i))
                                .product(options.get(i).getProduct())
                                .orderDetailCount(i + 1)
                                .orderDetailPrice((i + 1) * 10000)
                                .build()
                );
                productCount += i + 1;
                totalPrice += (i + 1) * 10000;
            }

            ProductOrder order = ProductOrder.builder()
                    .member(m)
                    .recipient(m.getUserName())
                    .orderPhone("010-1234-" + phoneSuffix++)
                    .orderAddress(m.getUserName() + " address")
                    .orderMemo(m.getUserName() + " memo")
                    .orderTotalPrice(totalPrice)
                    .deliveryFee(totalPrice < 100000 ? 3500 : 0)
                    .paymentType("card")
                    .orderStat(OrderStatus.ORDER.getStatusStr())
                    .productCount(productCount)
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .build();

            details.forEach(order::addDetail);
            result.add(order);
        }

        return result;
    }

    // 저장되지 않은 상태의 ProductOrder, ProductOrderDetail 데이터 생성. 배송 완료 상태의 데이터.
    public static List<ProductOrder> createCompleteProductOrder(List<Member> members, List<ProductOption> options) {
        List<ProductOrder> result = new ArrayList<>();
        for(Member m : members) {
            int optionCount = randomInt();
            int productCount = 0;
            int totalPrice = 0;
            int phoneSuffix = 2345;
            List<ProductOrderDetail> details = new ArrayList<>();
            for(int i = 0; i < optionCount; i++) {
                details.add(
                        ProductOrderDetail.builder()
                                .productOption(options.get(i))
                                .product(options.get(i).getProduct())
                                .orderDetailCount(i + 1)
                                .orderDetailPrice((i + 1) * 10000)
                                .build()
                );
                productCount += i + 1;
                totalPrice += (i + 1) * 10000;
            }

            ProductOrder order = ProductOrder.builder()
                    .member(m)
                    .recipient(m.getUserName())
                    .orderPhone("010-1234-" + phoneSuffix++)
                    .orderAddress(m.getUserName() + " address")
                    .orderMemo(m.getUserName() + " memo")
                    .orderTotalPrice(totalPrice)
                    .deliveryFee(totalPrice < 100000 ? 3500 : 0)
                    .paymentType("card")
                    .orderStat(OrderStatus.COMPLETE.getStatusStr())
                    .productCount(productCount)
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .build();

            details.forEach(order::addDetail);
            result.add(order);
        }

        return result;
    }

    // 저장 상태의 ProductOrder, ProductOrderDetail 데이터 생성
    public static List<ProductOrder> createSaveProductOrder(List<Member> members, List<ProductOption> options) {
        List<ProductOrder> result = new ArrayList<>();
        long orderId = 1L;
        long detailId = 1L;
        for(Member m : members) {
            int optionCount = randomInt();
            int productCount = 0;
            int totalPrice = 0;
            int phoneSuffix = 2345;
            List<ProductOrderDetail> details = new ArrayList<>();
            for(int i = 0; i < optionCount; i++) {
                details.add(
                        ProductOrderDetail.builder()
                                .id(detailId)
                                .productOption(options.get(i))
                                .product(options.get(i).getProduct())
                                .orderDetailCount(i + 1)
                                .orderDetailPrice((i + 1) * 10000)
                                .build()
                );
                productCount += i + 1;
                totalPrice += (i + 1) * 10000;
                detailId++;
            }

            ProductOrder order = ProductOrder.builder()
                    .id(orderId)
                    .member(m)
                    .recipient(m.getUserName())
                    .orderPhone("010-1234" + phoneSuffix++)
                    .orderAddress(m.getUserName() + " address")
                    .orderMemo(m.getUserName() + " memo")
                    .orderTotalPrice(totalPrice)
                    .deliveryFee(totalPrice < 100000 ? 3500 : 0)
                    .paymentType("card")
                    .orderStat(OrderStatus.ORDER.getStatusStr())
                    .productCount(productCount)
                    .build();

            details.forEach(order::addDetail);
            result.add(order);
            orderId++;
        }

        return result;
    }

    public static List<ProductOrder> createAnonymousProductOrder(Member anonymous, List<ProductOption> options) {
        List<ProductOrder> result = new ArrayList<>();
        String anonymousName = "미가입자";
        for(int i = 0; i < 5; i++) {
            int optionCount = randomInt();
            int productCount = 0;
            int totalPrice = 0;
            int phoneSuffix = 2345;
            List<ProductOrderDetail> details = new ArrayList<>();
            for(int j = 0; j < optionCount; j++) {
                details.add(
                        ProductOrderDetail.builder()
                                .productOption(options.get(j))
                                .product(options.get(i).getProduct())
                                .orderDetailCount(j + 1)
                                .orderDetailPrice((j + 1) * 10000)
                                .build()
                );
                productCount += j + 1;
                totalPrice += (j + 1) * 10000;
            }

            ProductOrder order = ProductOrder.builder()
                    .member(anonymous)
                    .recipient(anonymousName + i)
                    .orderPhone("010-9876" + phoneSuffix)
                    .orderAddress(anonymousName + " address")
                    .orderMemo(anonymousName + " memo")
                    .orderTotalPrice(totalPrice)
                    .deliveryFee(totalPrice < 100000 ? 3500 : 0)
                    .paymentType("card")
                    .orderStat(OrderStatus.COMPLETE.getStatusStr())
                    .productCount(productCount)
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .build();

            phoneSuffix++;
            details.forEach(order::addDetail);
            result.add(order);
        }

        return result;
    }

    private static int randomInt() {
        Random ran = new Random();

        return ran.nextInt(4) + 1;
    }
}
