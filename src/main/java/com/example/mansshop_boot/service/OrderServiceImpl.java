package com.example.mansshop_boot.service;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.config.customException.exception.CustomNotFoundException;
import com.example.mansshop_boot.config.customException.exception.CustomOrderDataFailedException;
import com.example.mansshop_boot.config.customException.exception.CustomOrderSessionExpiredException;
import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.fallback.FallbackProperties;
import com.example.mansshop_boot.domain.dto.order.business.*;
import com.example.mansshop_boot.domain.dto.order.in.OrderProductDTO;
import com.example.mansshop_boot.domain.dto.order.in.OrderProductRequestDTO;
import com.example.mansshop_boot.domain.dto.order.in.PaymentDTO;
import com.example.mansshop_boot.domain.dto.order.out.OrderDataResponseDTO;
import com.example.mansshop_boot.domain.dto.rabbitMQ.RabbitMQProperties;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.FallbackMapKey;
import com.example.mansshop_boot.domain.enumeration.RabbitMQPrefix;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.domain.vo.order.OrderItemVO;
import com.example.mansshop_boot.domain.vo.order.PreOrderDataVO;
import com.example.mansshop_boot.repository.cart.CartDetailRepository;
import com.example.mansshop_boot.repository.cart.CartRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderRepository;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService{

	private static final Logger failedOrderLogger = LoggerFactory.getLogger("com.example.mansshop.order.failed");

    private final ProductOrderRepository productOrderRepository;

    private final CartDetailRepository cartDetailRepository;

    private final CartRepository cartRepository;

    private final ProductOptionRepository productOptionRepository;

    private final ProductRepository productRepository;

    private final RabbitTemplate rabbitTemplate;

    private final RabbitMQProperties rabbitMQProperties;

    private final RedisTemplate<String, PreOrderDataVO> orderRedisTemplate;

	private final RedisTemplate<String, FailedOrderDTO> failedOrderRedisTemplate;

	private final FallbackProperties fallbackProperties;

	private static final String ANONYMOUS = "Anonymous";

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
    @Transactional(rollbackFor = RuntimeException.class)
    public String payment(PaymentDTO paymentDTO, CartMemberDTO cartMemberDTO) {
		boolean successFlag = false;

		try {
			ProductOrderDataDTO productOrderDataDTO = createOrderDataDTO(paymentDTO, cartMemberDTO, LocalDateTime.now());
			ProductOrder order = productOrderDataDTO.productOrder();

			productOrderRepository.save(order);
			successFlag = true;

			sendOrderQueueMessage(paymentDTO, cartMemberDTO, productOrderDataDTO, order);

			return Result.OK.getResultKey();
		}catch (Exception e) {
			log.error("payment Error : ", e);
			if(!successFlag) {
				log.error("handleFallback call");
				handleOrderFallback(paymentDTO, cartMemberDTO, e);
				throw new CustomOrderDataFailedException(ErrorCode.ORDER_DATA_FAILED, ErrorCode.ORDER_DATA_FAILED.getMessage());
			}else {
				log.error("payment Message Queue Error : ", e);
				handleOrderMQFallback(paymentDTO, cartMemberDTO, e);
				return Result.OK.getResultKey();
			}
		}
    }

	@Override
	public String retryFailedOrder(FailedOrderDTO failedOrderDTO, FallbackMapKey fallbackMapKey) {
		try {
			ProductOrderDataDTO productOrderDataDTO = createOrderDataDTO(failedOrderDTO.paymentDTO(), failedOrderDTO.cartMemberDTO(), failedOrderDTO.failedTime());
			ProductOrder order = productOrderDataDTO.productOrder();
			String exchange = getOrderExchange();
			if(fallbackMapKey == FallbackMapKey.ORDER)
				sendMessage(exchange, RabbitMQPrefix.QUEUE_FAILED_ORDER, productOrderDataDTO);
			sendOrderQueueMessage(failedOrderDTO.paymentDTO(), failedOrderDTO.cartMemberDTO(), productOrderDataDTO, order);

			return Result.OK.getResultKey();
		}catch (Exception e) {
			log.error("retry payment Message Queue Error : ", e);
			handleOrderMQFallback(failedOrderDTO.paymentDTO(), failedOrderDTO.cartMemberDTO(), e);
			throw new CustomOrderDataFailedException(ErrorCode.ORDER_DATA_FAILED, ErrorCode.ORDER_DATA_FAILED.getMessage());
		}
	}


	private void sendOrderQueueMessage(PaymentDTO paymentDTO, CartMemberDTO cartMemberDTO, ProductOrderDataDTO productOrderDataDTO, ProductOrder order) throws Exception {
		String orderExchange = getOrderExchange();

		if(paymentDTO.orderType().equals("cart"))
			sendMessage(orderExchange, RabbitMQPrefix.QUEUE_ORDER_CART, new OrderCartDTO(cartMemberDTO, productOrderDataDTO.orderOptionIds()));

		sendMessage(orderExchange, RabbitMQPrefix.QUEUE_ORDER_PRODUCT_OPTION, new OrderProductMessageDTO(productOrderDataDTO));
		sendMessage(orderExchange, RabbitMQPrefix.QUEUE_ORDER_PRODUCT, new OrderProductMessageDTO(productOrderDataDTO));
		sendMessage(orderExchange, RabbitMQPrefix.QUEUE_PERIOD_SUMMARY, new PeriodSummaryQueueDTO(order));
		sendMessage(orderExchange, RabbitMQPrefix.QUEUE_PRODUCT_SUMMARY, new OrderProductSummaryDTO(productOrderDataDTO));
	}

	private String getOrderExchange() {
		return rabbitMQProperties.getExchange()
								.get(RabbitMQPrefix.EXCHANGE_ORDER.getKey())
								.getName();
	}

    private String getQueueRoutingKey(RabbitMQPrefix rabbitMQPrefix) {
        return rabbitMQProperties.getQueue()
                                .get(rabbitMQPrefix.getKey())
                                .getRouting();
    }

	private <T> void sendMessage(String exchange, RabbitMQPrefix rabbitMQPrefix, T data) {
		rabbitTemplate.convertAndSend(
				exchange,
				getQueueRoutingKey(rabbitMQPrefix),
				data
		);
	}

	private void handleOrderFallback(PaymentDTO paymentDTO, CartMemberDTO cartMemberDTO, Exception e) {
		orderRedisFallbackProcess(paymentDTO, cartMemberDTO, e, FallbackMapKey.ORDER);
	}

	private void handleOrderMQFallback(PaymentDTO paymentDTO, CartMemberDTO cartMemberDTO, Exception e) {
		orderRedisFallbackProcess(paymentDTO, cartMemberDTO, e, FallbackMapKey.ORDER_MESSAGE);
	}

	private void orderRedisFallbackProcess(PaymentDTO paymentDTO, CartMemberDTO cartMemberDTO, Exception e, FallbackMapKey fallbackMapKey) {
		FailedOrderDTO failedDTO = new FailedOrderDTO(paymentDTO, cartMemberDTO, LocalDateTime.now(), e.getMessage());
		ObjectMapper om = new ObjectMapper();
		try {
			String randomString = UUID.randomUUID().toString();
			String keyPrefix = fallbackProperties.getRedis().get(fallbackMapKey.getKey()).getPrefix();
			String orderKey = keyPrefix.concat(randomString);

			failedOrderRedisTemplate.opsForValue().set(orderKey, failedDTO);
		}catch (Exception e1) {
			try {
				failedOrderLogger.error("handleOrderFallback Error :: request Data : {}", om.writeValueAsString(failedDTO));
			}catch (JsonProcessingException e2) {
				failedOrderLogger.error("handleOrderFallback Error :: JsonProcessingException - request Data : {}", failedDTO);
			}
			log.error("handleOrderFallback Error Message : ", e1);
		}
	}


    public ProductOrderDataDTO createOrderDataDTO(PaymentDTO paymentDTO, CartMemberDTO cartMemberDTO, LocalDateTime createdAt) {
        ProductOrder productOrder = paymentDTO.toOrderEntity(cartMemberDTO.uid(), createdAt);
        List<OrderProductDTO> orderProductList = paymentDTO.orderProduct();
        List<String> orderProductIds = new ArrayList<>();// 주문한 상품 옵션 아이디를 담아줄 리스트
        List<Long> orderOptionIds = new ArrayList<>();
        int totalProductCount = 0;// 총 판매량
        //옵션 정보 리스트에서 각 객체를 OrderDetail Entity로 Entity화 해서 ProductOrder Entity에 담아준다.
        //주문한 옵션 번호는 추후 더 사용하기 때문에 리스트화 한다.
        //총 판매량은 기간별 매출에 필요하기 때문에 이때 같이 총 판매량을 계산한다.
        for(OrderProductDTO data : paymentDTO.orderProduct()) {
            productOrder.addDetail(data.toOrderDetailEntity());
            if(!orderProductIds.contains(data.getProductId()))
                orderProductIds.add(data.getProductId());
            orderOptionIds.add(data.getOptionId());
            totalProductCount += data.getDetailCount();
        }
        productOrder.setProductCount(totalProductCount);

        return new ProductOrderDataDTO(productOrder, orderProductList, orderProductIds, orderOptionIds);
    }

    @Override
    public OrderDataResponseDTO getProductOrderData(List<OrderProductRequestDTO> optionIdAndCountDTO, 
													HttpServletRequest request, 
													HttpServletResponse response, 
													Principal principal) {

        List<OrderProductInfoDTO> orderProductInfoDTO = getOrderDataDTOList(optionIdAndCountDTO);

        if(orderProductInfoDTO.isEmpty())
            throw new IllegalArgumentException("product Order Data is empty");

        OrderDataResponseDTO responseDTO = mappingOrderResponseDTO(optionIdAndCountDTO, orderProductInfoDTO);
		String userId = principal != null ? principal.getName() : ANONYMOUS;
        saveOrderValidateData(responseDTO, request, response, userId);

        return responseDTO;
    }

    @Override
    public OrderDataResponseDTO getCartOrderData(List<Long> cartDetailIds, 
												CartMemberDTO cartMemberDTO, 
												HttpServletRequest request, 
												HttpServletResponse response) {
        List<CartDetail> cartDetails = cartDetailRepository.findAllById(cartDetailIds);

        if(cartDetails.isEmpty())
            throw new CustomNotFoundException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());

        Long cartId = cartDetails.get(0).getCart().getId();
        Cart cart = cartRepository.findById(cartId).orElseThrow(IllegalArgumentException::new);

        if(!cart.getMember().getUserId().equals(cartMemberDTO.uid())
                || !Objects.equals(cart.getCookieId(), cartMemberDTO.cartCookieValue()))
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

        OrderDataResponseDTO responseDTO = mappingOrderResponseDTO(optionIdAndCountDTO, orderProductInfoDTO);
        saveOrderValidateData(responseDTO, request, response, cartMemberDTO.uid());

        return responseDTO;
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

	// 주문 검증 데이터 저장 및 토큰 생성, 쿠키 생성
	private void saveOrderValidateData(OrderDataResponseDTO requestDTO,
									   HttpServletRequest request,
									   HttpServletResponse response,
									   String userId) {
		String originToken = getOrderToken(request);

		if(originToken != null)
			deleteTokenData(originToken);

		String orderToken = createOrderToken();
		setOrderToken(response, orderToken);
		List<OrderItemVO> orderItemVOList = requestDTO.orderData().stream().map(OrderDataDTO::toOrderItemVO).toList();
		PreOrderDataVO preOrderDataVO = new PreOrderDataVO(userId, orderItemVOList, requestDTO.totalPrice());
		orderRedisTemplate.opsForValue().set(orderToken, preOrderDataVO, Duration.ofMinutes(10));
	}

	public void deleteTokenData(String token) {
		orderRedisTemplate.delete(token);
	}

	// 결제 API 호출 전 데이터 검증
	@Override
	public ResponseMessageDTO validateOrder(OrderDataResponseDTO requestDTO, 
											Principal principal, 
											HttpServletRequest request, 
											HttpServletResponse response) {
		ObjectMapper om = new ObjectMapper();
		String orderToken = getOrderToken(request);
		String uid = principal != null ? principal.getName() : ANONYMOUS;
		
		if(orderToken == null){
			log.warn("Order Session Expired. orderToken is null");
			throw new CustomOrderSessionExpiredException(
				ErrorCode.ORDER_SESSION_EXPIRED, 
				ErrorCode.ORDER_SESSION_EXPIRED.getMessage()
			);
		}
		
		PreOrderDataVO preOrderDataVO = orderRedisTemplate.opsForValue().get(orderToken);

		if(preOrderDataVO == null){
			log.warn("Order Session Expired. Validate Data is null - token: {}", orderToken);
			deleteOrderToken(response);
			throw new CustomOrderSessionExpiredException(
				ErrorCode.ORDER_SESSION_EXPIRED, 
				ErrorCode.ORDER_SESSION_EXPIRED.getMessage()
			);
		}
		
		if(!validateOrderData(preOrderDataVO, requestDTO, uid)){
			try {
				deleteOrderToken(response);
				log.error("Order Data Validation Failed - token: {}, userId: {}, submittedData: {}, redisData: {}",
						orderToken, uid, om.writeValueAsString(requestDTO), om.writeValueAsString(preOrderDataVO));
			} catch (JsonProcessingException e) {
				log.error("OrderService.validateOrder :: JsonProcessingException - Order Data Validation Failed - token: {}, userId: {}, submittedData: {}, redisData: {}",
							orderToken, uid, requestDTO, preOrderDataVO);
			}
			throw new CustomOrderSessionExpiredException(
				ErrorCode.ORDER_SESSION_EXPIRED, 
				ErrorCode.ORDER_SESSION_EXPIRED.getMessage()
			);
		}

		return new ResponseMessageDTO(Result.OK.getResultKey());
	}

	// 요청 데이터와 Redis 데이터 비교 검증
	private boolean validateOrderData(PreOrderDataVO preOrderDataVO, OrderDataResponseDTO requestDTO, String uid) {
		if(!preOrderDataVO.userId().equals(uid) || 
				preOrderDataVO.totalPrice() != requestDTO.totalPrice()){
			log.error("Order Data Validation Failed. UserId or TotalPrice is different - userId: {}, validatePrice: {}, requestPrice: {}",
					uid, preOrderDataVO.totalPrice(), requestDTO.totalPrice());
			return false;
		}
		
		List<OrderItemVO> preOrderData = preOrderDataVO.orderData();
		List<OrderDataDTO> requestData = requestDTO.orderData();

		for(OrderDataDTO data : requestData) {
			OrderItemVO dataVO = data.toOrderItemVO();
			if(!preOrderData.contains(dataVO))
				return false;
		}

		return true;
	}

	//Order Token 생성
	private String createOrderToken() {
		return UUID.randomUUID().toString();
	}

	//Order Token 조회
	private String getOrderToken(HttpServletRequest request) {
		Cookie orderToken = WebUtils.getCookie(request, "order");
		if(orderToken == null)
			return null;

		return orderToken.getValue();
	}

	//Order Token 쿠키에 저장
	//유효기간 10분, secure, httpOnly, sameSite Strict 설정
	private void setOrderToken(HttpServletResponse response, String orderToken) {
		response.addHeader(
			"Set-Cookie",
			createOrderTokenCookie(orderToken, Duration.ofMinutes(10))
		);
	}

	//Order Token 쿠키 생성
	private String createOrderTokenCookie(String orderToken, Duration maxAge) {
		return ResponseCookie
					.from("order", orderToken)
					.path("/")
					.maxAge(maxAge)
					.secure(true)
					.httpOnly(true)
					.sameSite("Strict")
					.build()
					.toString();
	}

	//Order Token 쿠키 삭제
	private void deleteOrderToken(HttpServletResponse response) {
		response.addHeader(
			"Set-Cookie",
			createOrderTokenCookie("", Duration.ZERO)
		);
	}
}
