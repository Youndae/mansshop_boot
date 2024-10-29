package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.order.in.OrderProductRequestDTO;
import com.example.mansshop_boot.domain.dto.order.in.PaymentDTO;
import com.example.mansshop_boot.domain.dto.order.out.OrderDataResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.service.CartService;
import com.example.mansshop_boot.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    private final CartService cartService;

    /**
     *
     * @param paymentDTO
     * @param request
     * @param principal
     *
     * 결제 완료 이후 주문 정보 처리
     */
    @PostMapping("/")
    public ResponseEntity<?> payment(@RequestBody PaymentDTO paymentDTO
                                    , HttpServletRequest request
                                    , Principal principal) {

        CartMemberDTO cartMemberDTO = cartService.getCartMemberDTO(request, principal);

        String responseMessage = orderService.payment(paymentDTO, cartMemberDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    @PostMapping("/product")
    public ResponseEntity<OrderDataResponseDTO> orderProduct(@RequestBody List<OrderProductRequestDTO> requestDTO){
        /**
         * 여기랑 cart 테스트.
         * 이후 cartDetail 테이블에서 cartPrice 삭제.
         * 아마 장바구니 페이지에서만 필요할텐데 전체적으로 한번 훑어보는 정도로만 체크.
         * 끝나면 관리자 리뷰 기능 추가.
         */
        OrderDataResponseDTO responseDTO = orderService.getProductOrderData(requestDTO);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/cart")
    public ResponseEntity<OrderDataResponseDTO> orderCart(@RequestBody List<Long> cartDetailIds
                                        , HttpServletRequest request
                                        , Principal principal) {

        CartMemberDTO cartMemberDTO = cartService.getCartMemberDTO(request, principal);
        OrderDataResponseDTO responseDTO = orderService.getCartOrderData(cartDetailIds, cartMemberDTO);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

}
