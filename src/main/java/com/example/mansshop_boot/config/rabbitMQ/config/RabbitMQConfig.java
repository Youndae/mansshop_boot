package com.example.mansshop_boot.config.rabbitMQ.config;

import com.example.mansshop_boot.domain.dto.rabbitMQ.RabbitMQProperties;
import com.example.mansshop_boot.domain.enumuration.RabbitMQPrefix;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.Map;

@Configuration
public class RabbitMQConfig {

    private final Map<String, RabbitMQProperties.Exchange> exchange;

    private final Map<String, RabbitMQProperties.Queue> queue;

    public RabbitMQConfig(RabbitMQProperties rabbitMQProperties) {
        this.exchange = rabbitMQProperties.getExchange();
        this.queue = rabbitMQProperties.getQueue();
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());

        return rabbitTemplate;
    }

    @DependsOn("orderDLQExchange")
    private Queue createQueueWithDLQExchange(String queueKey, String dlqExchangeKey) {
        return QueueBuilder.durable(queue.get(queueKey).getName())
                .withArgument("x-dead-letter-exchange", exchange.get(dlqExchangeKey).getDlq())
                .withArgument("x-dead-letter-routing-key", queue.get(queueKey).getDlqRouting())
                .build();
    }

    private Queue createQueue(String queueName) {
        return QueueBuilder.durable(queueName).build();
    }

    private DirectExchange createExchange(String exchangeName, boolean durable) {
        return ExchangeBuilder.directExchange(exchangeName)
                                .durable(durable)
                                .build();
    }

    private Binding setBinding(Queue bindingQueue, DirectExchange exchange, String routingKey) {
        return BindingBuilder.bind(bindingQueue)
                            .to(exchange)
                            .with(routingKey);
    }

    @Bean
    public Queue orderProductQueue() {
        return createQueueWithDLQExchange(RabbitMQPrefix.QUEUE_ORDER_PRODUCT.getKey(), RabbitMQPrefix.EXCHANGE_ORDER.getKey());
    }

    @Bean
    public Queue orderProductOptionQueue() {
        return createQueueWithDLQExchange(RabbitMQPrefix.QUEUE_ORDER_PRODUCT_OPTION.getKey(), RabbitMQPrefix.EXCHANGE_ORDER.getKey());
    }

    @Bean
    public Queue periodSummaryQueue() {
        return createQueueWithDLQExchange(RabbitMQPrefix.QUEUE_PERIOD_SUMMARY.getKey(), RabbitMQPrefix.EXCHANGE_ORDER.getKey());
    }

    @Bean
    public Queue productSummaryQueue() {
        return createQueueWithDLQExchange(RabbitMQPrefix.QUEUE_PRODUCT_SUMMARY.getKey(), RabbitMQPrefix.EXCHANGE_ORDER.getKey());
    }

    @Bean
    public Queue orderCartQueue() {
        return createQueueWithDLQExchange(RabbitMQPrefix.QUEUE_ORDER_CART.getKey(), RabbitMQPrefix.EXCHANGE_ORDER.getKey());
    }

    @Bean
    public DirectExchange orderExchange() {
        return createExchange(exchange.get(RabbitMQPrefix.EXCHANGE_ORDER.getKey()).getName(), true);
    }

    @Bean
    public Binding orderProductBinding() {

        return setBinding(orderProductQueue(),
                orderExchange(),
                queue.get(RabbitMQPrefix.QUEUE_ORDER_PRODUCT.getKey()).getRouting()
        );
    }

    @Bean
    public Binding orderProductOptionBinding() {

        return setBinding(orderProductOptionQueue(),
                orderExchange(),
                queue.get(RabbitMQPrefix.QUEUE_ORDER_PRODUCT_OPTION.getKey()).getRouting()
        );
    }

    @Bean
    public Binding periodSummaryBinding() {

        return setBinding(periodSummaryQueue(),
                orderExchange(),
                queue.get(RabbitMQPrefix.QUEUE_PERIOD_SUMMARY.getKey()).getRouting()
        );
    }

    @Bean
    public Binding productSummaryBinding() {

        return setBinding(productSummaryQueue(),
                orderExchange(),
                queue.get(RabbitMQPrefix.QUEUE_PRODUCT_SUMMARY.getKey()).getRouting()
        );
    }

    @Bean
    public Binding orderCartBinding() {

        return setBinding(orderCartQueue(),
                orderExchange(),
                queue.get(RabbitMQPrefix.QUEUE_ORDER_CART.getKey()).getRouting()
        );
    }

    @Bean
    public Queue orderProductDLQ() {
        return createQueue(queue.get(RabbitMQPrefix.QUEUE_ORDER_PRODUCT.getKey()).getDlq());
    }

    @Bean
    public Queue orderProductOptionDLQ() {
        return createQueue(queue.get(RabbitMQPrefix.QUEUE_ORDER_PRODUCT_OPTION.getKey()).getDlq());
    }

    @Bean
    public Queue periodSummaryDLQ() {
        return createQueue(queue.get(RabbitMQPrefix.QUEUE_PERIOD_SUMMARY.getKey()).getDlq());
    }

    @Bean
    public Queue productSummaryDLQ() {
        return createQueue(queue.get(RabbitMQPrefix.QUEUE_PRODUCT_SUMMARY.getKey()).getDlq());
    }

    @Bean
    public Queue orderCartDLQ() {
        return createQueue(queue.get(RabbitMQPrefix.QUEUE_ORDER_CART.getKey()).getDlq());
    }

    @Bean
    public DirectExchange orderDLQExchange() {
        return createExchange(exchange.get(RabbitMQPrefix.EXCHANGE_ORDER.getKey()).getDlq(), true);
    }

    @Bean
    public Binding orderProductDLQBinding() {

        return setBinding(orderProductDLQ(),
                orderDLQExchange(),
                queue.get(RabbitMQPrefix.QUEUE_ORDER_PRODUCT.getKey()).getDlqRouting()
        );
    }

    @Bean
    public Binding orderProductOptionDLQBinding() {

        return setBinding(orderProductOptionDLQ(),
                orderDLQExchange(),
                queue.get(RabbitMQPrefix.QUEUE_ORDER_PRODUCT_OPTION.getKey()).getDlqRouting()
        );
    }

    @Bean
    public Binding periodSummaryDLQBinding() {

        return setBinding(periodSummaryDLQ(),
                orderDLQExchange(),
                queue.get(RabbitMQPrefix.QUEUE_PERIOD_SUMMARY.getKey()).getDlqRouting()
        );
    }

    @Bean
    public Binding productSummaryDLQBinding() {

        return setBinding(productSummaryDLQ(),
                createExchange(exchange.get(RabbitMQPrefix.EXCHANGE_ORDER.getKey()).getDlq(), true),
                queue.get(RabbitMQPrefix.QUEUE_PRODUCT_SUMMARY.getKey()).getDlqRouting()
        );
    }

    @Bean
    public Binding orderCartDLQBinding() {

        return setBinding(orderCartDLQ(),
                createExchange(exchange.get(RabbitMQPrefix.EXCHANGE_ORDER.getKey()).getDlq(), true),
                queue.get(RabbitMQPrefix.QUEUE_ORDER_CART.getKey()).getDlqRouting()
        );
    }
}
