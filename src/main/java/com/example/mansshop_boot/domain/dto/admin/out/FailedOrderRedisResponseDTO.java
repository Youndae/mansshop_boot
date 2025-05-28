package com.example.mansshop_boot.domain.dto.admin.out;

import com.example.mansshop_boot.domain.dto.order.business.FailedOrderDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FailedOrderRedisResponseDTO {
    private LocalDateTime failedDate;

    private String orderUserId;

    private long totalPrice;

    private String paymentType;

    private String failedMessage;

    public FailedOrderRedisResponseDTO(FailedOrderDTO failedOrderDTO) {
        this.failedDate = failedOrderDTO.failedTime();
        this.orderUserId = failedOrderDTO.cartMemberDTO().uid();
        this.totalPrice = failedOrderDTO.paymentDTO().totalPrice();
        this.paymentType = failedOrderDTO.paymentDTO().paymentType();
        this.failedMessage = failedOrderDTO.message();
    }
}
