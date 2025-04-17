package com.example.mansshop_boot.domain.dto.order.business;

import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderCartDTO{
        private CartMemberDTO cartMemberDTO;
        private List<Long> productOptionIds;
}
