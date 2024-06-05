package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.cart.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.order.PaymentDTO;
import com.example.mansshop_boot.service.CartService;
import com.example.mansshop_boot.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    private final CartService cartService;

    @PostMapping("/")
    public ResponseEntity<?> payment(@RequestBody PaymentDTO paymentDTO
                                    , HttpServletRequest request
                                    , Principal principal) {

        CartMemberDTO cartMemberDTO = cartService.getCartMemberDTO(request, principal);
        log.info("orderController payment :: paymentDTO : {}", paymentDTO);

        return orderService.payment(paymentDTO, cartMemberDTO);
    }
}
