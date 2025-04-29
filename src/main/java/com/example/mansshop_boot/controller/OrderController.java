package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.annotation.swagger.DefaultApiResponse;
import com.example.mansshop_boot.annotation.swagger.SwaggerAuthentication;
import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.order.in.OrderProductRequestDTO;
import com.example.mansshop_boot.domain.dto.order.in.PaymentDTO;
import com.example.mansshop_boot.domain.dto.order.out.OrderDataResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.service.CartService;
import com.example.mansshop_boot.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
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
    @Operation(summary = "결제 완료 이후 주문 데이터 처리")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(
            name = "cartCookie",
            description = "비회원이면서 장바구니를 통한 결제를 한 경우 JWT가 아닌 이 쿠키값이 필요.",
            in = ParameterIn.COOKIE
    )
    @PostMapping("/")
    public ResponseEntity<?> payment(@RequestBody PaymentDTO paymentDTO,
                                    HttpServletRequest request,
                                    Principal principal) {

        CartMemberDTO cartMemberDTO = cartService.getCartMemberDTO(request, principal);

        String responseMessage = orderService.payment(paymentDTO, cartMemberDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param requestDTO
     *
     * 상품 상세 페이지에서 주문 요청 시 해당 상품 데이터를 조회해서 반환
     */
    @Operation(summary = "상품 상세 페이지에서 결제 요청 시 상품 결제 정보 반환")
    @DefaultApiResponse
    @SwaggerAuthentication
    @PostMapping("/product")
    public ResponseEntity<OrderDataResponseDTO> orderProduct(@Schema(name = "주문 요청 상품 데이터", type = "array") @RequestBody List<OrderProductRequestDTO> requestDTO){
        /**
         * 여기랑 cart 테스트.
         * 이후 cartDetail 테이블에서 cartPrice 삭제.
         * 아마 장바구니 페이지에서만 필요할텐데 전체적으로 한번 훑어보는 정도로만 체크.
         * 끝나면 관리자 리뷰 기능 추가.
         */
        OrderDataResponseDTO responseDTO = orderService.getProductOrderData(requestDTO);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @Operation(summary = "상품 상세 페이지에서 결제 요청 시 상품 결제 정보 반환")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(
            name = "cartCookie",
            description = "비회원인 경우 갖게 되는 장바구니 cookieId. 비회원의 경우 JWT가 아닌 이 쿠키값이 필요.",
            in = ParameterIn.COOKIE
    )
    @PostMapping("/cart")
    public ResponseEntity<OrderDataResponseDTO> orderCart(@Schema(name = "장바구니 상세 데이터 아이디 리스트", type = "array")
                                                          @RequestBody List<Long> cartDetailIds,
                                                            HttpServletRequest request,
                                                            Principal principal) {

        CartMemberDTO cartMemberDTO = cartService.getCartMemberDTO(request, principal);
        OrderDataResponseDTO responseDTO = orderService.getCartOrderData(cartDetailIds, cartMemberDTO);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

}
