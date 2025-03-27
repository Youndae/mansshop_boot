package com.example.mansshop_boot.domain.entity;

import com.example.mansshop_boot.domain.dto.order.business.PeriodSummaryQueueDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "periodSalesSummary")
public class PeriodSalesSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate period;

    @Column(nullable = false)
    private long sales;

    @Column(nullable = false)
    private long salesQuantity;

    @Column(nullable = false)
    private long orderQuantity;

    @Column(columnDefinition = "BIGINT DEFAULT 0",
            nullable = false)
    private long totalDeliveryFee;

    @Column(columnDefinition = "BIGINT DEFAULT 0",
            nullable = false)
    private long cashTotal;

    @Column(columnDefinition = "BIGINT DEFAULT 0",
            nullable = false)
    private long cardTotal;

    public void setPatchData(PeriodSummaryQueueDTO dto) {
        this.period = dto.period();
        this.sales = this.sales + dto.sales();
        this.salesQuantity = this.salesQuantity + dto.salesQuantity();
        this.orderQuantity = this.orderQuantity + 1;
        this.totalDeliveryFee = this.totalDeliveryFee + dto.totalDeliveryFee();
        this.cashTotal = this.cashTotal + dto.cashSales();
        this.cardTotal = this.cardTotal + dto.cardSales();
    }
}
