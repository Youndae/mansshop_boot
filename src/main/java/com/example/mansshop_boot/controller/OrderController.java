package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.order.PaymentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    @PostMapping("/")
    public ResponseEntity<?> payment(@RequestBody PaymentDTO paymentDTO) {

        log.info("orderController payment :: paymentDTO : {}", paymentDTO);

        return null;
    }
}
