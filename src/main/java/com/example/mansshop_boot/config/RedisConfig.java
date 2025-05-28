package com.example.mansshop_boot.config;

import com.example.mansshop_boot.domain.dto.order.business.FailedOrderDTO;
import com.example.mansshop_boot.domain.vo.order.PreOrderDataVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    private <V> RedisTemplate<String, V> buildTemplate(RedisSerializer<V> redisSerializer) {
        RedisTemplate<String, V> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(redisSerializer);
        template.setValueSerializer(redisSerializer);

        return template;
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate() {

        return buildTemplate(new GenericJackson2JsonRedisSerializer());
    }

    @Bean
    public RedisTemplate<String, Long> longRedisTemplate() {

        return buildTemplate(new GenericToStringSerializer<>(Long.class));
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory());
        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setValueSerializer(new StringRedisSerializer());

        return stringRedisTemplate;
    }

    @Bean
    public RedisTemplate<String, PreOrderDataVO> objectRedisTemplate() {
        RedisTemplate<String, PreOrderDataVO> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, FailedOrderDTO> failedOrderRedisTemplate() {
        RedisTemplate<String, FailedOrderDTO> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Jackson2JsonRedisSerializer<FailedOrderDTO> serializer = new Jackson2JsonRedisSerializer<>(om, FailedOrderDTO.class);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);

        return redisTemplate;
    }
}
