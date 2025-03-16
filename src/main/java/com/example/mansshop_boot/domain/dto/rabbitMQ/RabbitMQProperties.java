package com.example.mansshop_boot.domain.dto.rabbitMQ;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMQProperties {
    private Map<String, Exchange> exchange;

    private Map<String, Queue> queue;

    public Map<String, Exchange> getExchange() {
        return exchange;
    }

    public void setExchange(Map<String, Exchange> exchange) {
        this.exchange = exchange;
    }

    public Map<String, Queue> getQueue() {
        return queue;
    }

    public void setQueue(Map<String, Queue> queue) {
        this.queue = queue;
    }

    public static class Exchange {

        private String name;

        private String dlq;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDlq() {
            return dlq;
        }

        public void setDlq(String dlq) {
            this.dlq = dlq;
        }

        @Override
        public String toString() {
            return "Exchange{" +
                    "name='" + name + '\'' +
                    ", dlq='" + dlq + '\'' +
                    '}';
        }
    }

    public static class Queue {

        private String name;

        private String routing;

        private String dlq;

        private String dlqRouting;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRouting() {
            return routing;
        }

        public void setRouting(String routing) {
            this.routing = routing;
        }

        public String getDlq() {
            return dlq;
        }

        public void setDlq(String dlq) {
            this.dlq = dlq;
        }

        public String getDlqRouting() {
            return dlqRouting;
        }

        public void setDlqRouting(String dlqRouting) {
            this.dlqRouting = dlqRouting;
        }

        @Override
        public String toString() {
            return "Queue{" +
                    "name='" + name + '\'' +
                    ", routing='" + routing + '\'' +
                    ", dlq='" + dlq + '\'' +
                    ", dlqRouting='" + dlqRouting + '\'' +
                    '}';
        }
    }
}
