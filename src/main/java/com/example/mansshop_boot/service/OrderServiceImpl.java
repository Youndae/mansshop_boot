package com.example.mansshop_boot.service;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.config.customException.exception.CustomNotFoundException;
import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.order.business.*;
import com.example.mansshop_boot.domain.dto.order.in.OrderProductDTO;
import com.example.mansshop_boot.domain.dto.order.in.OrderProductRequestDTO;
import com.example.mansshop_boot.domain.dto.order.in.PaymentDTO;
import com.example.mansshop_boot.domain.dto.order.out.OrderDataResponseDTO;
import com.example.mansshop_boot.domain.dto.rabbitMQ.RabbitMQProperties;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumuration.RabbitMQPrefix;
import com.example.mansshop_boot.domain.enumuration.Result;
import com.example.mansshop_boot.repository.cart.CartDetailRepository;
import com.example.mansshop_boot.repository.cart.CartRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService{

    private final ProductOrderRepository productOrderRepository;

    private final CartDetailRepository cartDetailRepository;

    private final CartRepository cartRepository;

    private final ProductOptionRepository productOptionRepository;

    private final ProductRepository productRepository;

    private final RabbitTemplate rabbitTemplate;

    private final RabbitMQProperties rabbitMQProperties;

    /**
     *
     * @param paymentDTO
     * @param cartMemberDTO
     * @return
     *
     * 결제 처리 이후 주문 데이터 처리.
     *
     * 주문 데이터인 ProductOrder, List<ProductOrderDetail> 저장 이후
     * 주문 타입 (direct, cart)에 따라 장바구니를 통한 주문인 경우 장바구니 데이터 삭제.
     * 상품 옵션별 재고 수정
     * 상품 판매량 수정
     * 기간별 매출 테이블 데이터 수정
     * 상품 옵션별 매출 테이블 데이터 수정 처리.
     *
     * 주문 데이터 처리를 제외한 나머지 처리들에 대해서는 RabbitMQ를 통한 비동기 처리.
     * 장바구니를 제외한 다른 RabbitMQ 요청은 동시성을 제어해야 하기 떄문에 concurrency 1 로 처리.
     *
     * 빠른 주문 데이터 처리를 위해 대부분의 로직은 Consumer에서 처리.
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class, propagation = Propagation.REQUIRED)
    public String payment(PaymentDTO paymentDTO, CartMemberDTO cartMemberDTO) {
        ProductOrderDataDTO productOrderDataDTO = createOrderDataDTO(paymentDTO, cartMemberDTO);
        ProductOrder order = productOrderDataDTO.productOrder();
        productOrderRepository.save(order);

        String orderExchange = rabbitMQProperties.getExchange()
                                                .get(RabbitMQPrefix.EXCHANGE_ORDER.getKey())
                                                .getName();

        //주문 타입이 cart인 경우 장바구니에서 선택한 상품 또는 전체 상품 주문이므로 해당 상품을 장바구니에서 삭제해준다.
        if(paymentDTO.orderType().equals("cart"))
            rabbitTemplate.convertAndSend(
                    orderExchange,
                    getQueueRoutingKey(RabbitMQPrefix.QUEUE_ORDER_CART),
                    new OrderCartDTO(cartMemberDTO, productOrderDataDTO.orderOptionIds())
            );

        // ProductOption의 재고 수정
        rabbitTemplate.convertAndSend(
                orderExchange,
                getQueueRoutingKey(RabbitMQPrefix.QUEUE_ORDER_PRODUCT_OPTION),
                productOrderDataDTO.orderProductList()
        );

        // Product의 salesQuantity 수정
        rabbitTemplate.convertAndSend(
                orderExchange,
                getQueueRoutingKey(RabbitMQPrefix.QUEUE_ORDER_PRODUCT),
                productOrderDataDTO.orderProductList()
        );

        // Period Summary 처리
        rabbitTemplate.convertAndSend(
                orderExchange,
                getQueueRoutingKey(RabbitMQPrefix.QUEUE_PERIOD_SUMMARY),
                new PeriodSummaryQueueDTO(order)
        );

        // Product Summary 처리
        rabbitTemplate.convertAndSend(
                orderExchange,
                getQueueRoutingKey(RabbitMQPrefix.QUEUE_PRODUCT_SUMMARY),
                new OrderProductSummaryDTO(productOrderDataDTO)
        );

        return Result.OK.getResultKey();
    }

    private String getQueueRoutingKey(RabbitMQPrefix rabbitMQPrefix) {
        return rabbitMQProperties.getQueue()
                                .get(rabbitMQPrefix.getKey())
                                .getRouting();
    }

    public ProductOrderDataDTO createOrderDataDTO(PaymentDTO paymentDTO, CartMemberDTO cartMemberDTO) {
        ProductOrder productOrder = paymentDTO.toOrderEntity(cartMemberDTO.uid());
        List<OrderProductDTO> orderProductList = paymentDTO.orderProduct();
        List<String> orderProductIds = new ArrayList<>();// 주문한 상품 옵션 아이디를 담아줄 리스트
        List<Long> orderOptionIds = new ArrayList<>();
        int totalProductCount = 0;// 총 판매량
        //옵션 정보 리스트에서 각 객체를 OrderDetail Entity로 Entity화 해서 ProductOrder Entity에 담아준다.
        //주문한 옵션 번호는 추후 더 사용하기 때문에 리스트화 한다.
        //총 판매량은 기간별 매출에 필요하기 때문에 이때 같이 총 판매량을 계산한다.
        for(OrderProductDTO data : paymentDTO.orderProduct()) {
            productOrder.addDetail(data.toOrderDetailEntity());
            if(!orderProductIds.contains(data.productId()))
                orderProductIds.add(data.productId());
            orderOptionIds.add(data.optionId());
            totalProductCount += data.detailCount();
        }
        productOrder.setProductCount(totalProductCount);

        return new ProductOrderDataDTO(productOrder, orderProductList, orderProductIds, orderOptionIds);
    }

    @Override
    public OrderDataResponseDTO getProductOrderData(List<OrderProductRequestDTO> optionIdAndCountDTO) {

        List<OrderProductInfoDTO> orderProductInfoDTO = getOrderDataDTOList(optionIdAndCountDTO);

        return mappingOrderResponseDTO(optionIdAndCountDTO, orderProductInfoDTO);
    }

    @Override
    public OrderDataResponseDTO getCartOrderData(List<Long> cartDetailIds, CartMemberDTO cartMemberDTO) {
        List<CartDetail> cartDetails = cartDetailRepository.findAllById(cartDetailIds);

        if(cartDetails.isEmpty())
            throw new CustomNotFoundException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());

        Long cartId = cartDetails.get(0).getCart().getId();
        Cart cart = cartRepository.findById(cartId).orElseThrow(IllegalArgumentException::new);

        if(!cart.getMember().getUserId().equals(cartMemberDTO.uid())
                && !cart.getCookieId().equals(cartMemberDTO.cartCookieValue()))
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());

        List<OrderProductRequestDTO> optionIdAndCountDTO = cartDetails.stream()
                                                                    .map(dto ->
                                                                            new OrderProductRequestDTO(
                                                                                    dto.getProductOption().getId()
                                                                                    , dto.getCartCount()
                                                                            )
                                                                    )
                                                                    .toList();

        List<OrderProductInfoDTO> orderProductInfoDTO = getOrderDataDTOList(optionIdAndCountDTO);

        return mappingOrderResponseDTO(optionIdAndCountDTO, orderProductInfoDTO);
    }

    public List<OrderProductInfoDTO> getOrderDataDTOList(List<OrderProductRequestDTO> optionIdAndCountDTO) {
        List<Long> optionIds = optionIdAndCountDTO.stream().map(OrderProductRequestDTO::optionId).toList();

        return productOptionRepository.findOrderData(optionIds);
    }

    public OrderDataResponseDTO mappingOrderResponseDTO(List<OrderProductRequestDTO> optionIdAndCountDTO, List<OrderProductInfoDTO> orderInfoList) {
        int totalPrice = 0;
        List<OrderDataDTO> orderDataDTOList = new ArrayList<>();

        for(OrderProductInfoDTO data : orderInfoList) {
            for(OrderProductRequestDTO dto : optionIdAndCountDTO){
                if(data.optionId() == dto.optionId()) {
                    OrderDataDTO orderData = new OrderDataDTO(data, dto.count());
                    orderDataDTOList.add(orderData);
                    totalPrice += orderData.price();
                }
            }
        }

        return new OrderDataResponseDTO(orderDataDTOList, totalPrice);
    }
}
