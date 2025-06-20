package com.example.mansshop_boot.service.integration.admin;

import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.order.business.*;
import com.example.mansshop_boot.domain.dto.order.in.OrderProductDTO;
import com.example.mansshop_boot.domain.dto.order.in.PaymentDTO;
import com.example.mansshop_boot.domain.dto.rabbitMQ.RabbitMQProperties;
import com.example.mansshop_boot.domain.entity.ProductOrder;
import com.example.mansshop_boot.domain.enumeration.RabbitMQPrefix;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
@ActiveProfiles("test")
public class AdminFailedDataServiceIT {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    private <T> void sendMessage(String exchange, RabbitMQPrefix rabbitMQPrefix, T data) {
        rabbitTemplate.convertAndSend(
                exchange,
                getQueueRoutingKey(rabbitMQPrefix),
                data
        );
    }

    private String getQueueRoutingKey(RabbitMQPrefix rabbitMQPrefix) {
        return rabbitMQProperties.getQueue()
                .get(rabbitMQPrefix.getKey())
                .getDlqRouting();
    }

    private String getOrderExchange() {
        return rabbitMQProperties.getExchange()
                .get(RabbitMQPrefix.EXCHANGE_ORDER.getKey())
                .getDlq();
    }

    private ProductOrderDataDTO createOrderDataDTO(PaymentDTO paymentDTO, CartMemberDTO cartMemberDTO, LocalDateTime createdAt) {
        ProductOrder productOrder = paymentDTO.toOrderEntity(cartMemberDTO.uid(), createdAt);
        List<OrderProductDTO> orderProductList = paymentDTO.orderProduct();
        List<String> orderProductIds = new ArrayList<>();
        List<Long> orderOptionIds = new ArrayList<>();
        int totalProductCount = 0;

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

    @Test
    @DisplayName(value = "DLQ 메시지 재시도")
    void retryFailedOrder() {
        /*String orderExchange = getOrderExchange();
        List<OrderProductDTO> orderProductList = createCartOrderProductDTOFixtureList();
        int totalPrice = orderProductList.stream().mapToInt(OrderProductDTO::getDetailPrice).sum();
        int deliveryFee = totalPrice < 100000 ? 3500 : 0;
        PaymentDTO paymentDTO = new PaymentDTO(
                "testRecipient",
                "01000010002",
                "testRecipient Memo",
                "testRecipient home",
                orderProductList,
                deliveryFee,
                totalPrice,
                "cash",
                "cart",
                productOptionList.size()
        );
        CartMemberDTO cartMemberDTO = new CartMemberDTO(member.getUserId(), null);
        ProductOrderDataDTO productOrderDataDTO = createOrderDataDTO(paymentDTO, cartMemberDTO, LocalDateTime.now());
        ProductOrder order = productOrderDataDTO.productOrder();

        sendMessage(orderExchange, RabbitMQPrefix.QUEUE_ORDER_CART, new OrderCartDTO(cartMemberDTO, productOrderDataDTO.orderOptionIds()));
        sendMessage(orderExchange, RabbitMQPrefix.QUEUE_ORDER_PRODUCT_OPTION, new OrderProductMessageDTO(productOrderDataDTO));
        sendMessage(orderExchange, RabbitMQPrefix.QUEUE_ORDER_PRODUCT, new OrderProductMessageDTO(productOrderDataDTO));
        sendMessage(orderExchange, RabbitMQPrefix.QUEUE_PERIOD_SUMMARY, new PeriodSummaryQueueDTO(order));
        sendMessage(orderExchange, RabbitMQPrefix.QUEUE_PRODUCT_SUMMARY, new OrderProductSummaryDTO(productOrderDataDTO));*/
    }
}
