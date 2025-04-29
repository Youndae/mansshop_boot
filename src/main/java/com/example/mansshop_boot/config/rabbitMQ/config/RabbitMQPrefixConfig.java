package com.example.mansshop_boot.config.rabbitMQ.config;

import com.example.mansshop_boot.domain.enumeration.RabbitMQPrefix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQPrefixConfig {
    @Bean(name = "rabbitMQPrefix")
    public Class<RabbitMQPrefix> rabbitMQPrefixClass() {
        return RabbitMQPrefix.class;
    }
}
