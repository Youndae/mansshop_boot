package com.example.mansshop_boot.domain.dto.rabbitMQ;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "rabbitmq")
@Getter
@Setter
public class RabbitMQProperties {
    private Map<String, Exchange> exchange;

    private Map<String, Queue> queue;

    @Getter
    @Setter
    public static class Exchange {

        private String name;

        private String dlq;
    }

    @Getter
    @Setter
    public static class Queue {

        private String name;

        private String routing;

        private String dlq;

        private String dlqRouting;
    }
}
