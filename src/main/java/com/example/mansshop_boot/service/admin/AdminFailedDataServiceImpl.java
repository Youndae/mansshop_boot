package com.example.mansshop_boot.service.admin;

import com.example.mansshop_boot.domain.dto.fallback.FallbackProperties;
import com.example.mansshop_boot.domain.dto.order.business.FailedOrderDTO;
import com.example.mansshop_boot.domain.dto.rabbitMQ.FailedQueueDTO;
import com.example.mansshop_boot.domain.dto.rabbitMQ.RabbitMQProperties;
import com.example.mansshop_boot.domain.enumeration.FallbackMapKey;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminFailedDataServiceImpl implements AdminFailedDataService {

    private final OrderService orderService;

    private final RabbitTemplate rabbitTemplate;

    private final RedisTemplate<String, FailedOrderDTO> failedOrderRedisTemplate;

    private final Jackson2JsonMessageConverter converter;

    private final RabbitMQProperties rabbitMQProperties;

    private final FallbackProperties fallbackProperties;

    @Value("${spring.rabbitmq.username}")
    private String rabbitMQUser;

    @Value("${spring.rabbitmq.password}")
    private String rabbitMQPw;

    /**
     *
     * RabbitMQ DLQ 메시지 조회
     * 개수만 조회하고 메시지가 존재하는 경우에만 DLQ 명과 함께 반환
     */
    @Override
    public List<FailedQueueDTO> getFailedMessageList() {
        List<String> dlqNames = rabbitMQProperties.getQueue().values().stream().map(RabbitMQProperties.Queue::getDlq).toList();
        List<FailedQueueDTO> result = new ArrayList<>();
        for(String name : dlqNames) {
            int messageCount = getFailedMessageCount(name);

            if(messageCount > 0)
                result.add(new FailedQueueDTO(name, messageCount));
        }

        return result;
    }

    /**
     *
     * @param queueName
     *
     * Queue 메시지 개수 조회
     */
    private int getFailedMessageCount(String queueName) {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:15672")
                .defaultHeaders(headers -> headers.setBasicAuth(rabbitMQUser, rabbitMQPw))
                .build();

        return (int) webClient.get()
                .uri(builder ->
                        builder.path("/api/queues/{vhost}/{queueNames}")
                                .build("/", queueName)
                )
                .retrieve()
                .bodyToMono(Map.class)
                .block()
                .get("messages");
    }

    /**
     *
     * @param queueDTOList
     *
     * DLQ 재시도 처리
     */
    @Override
    public String retryFailedMessages(List<FailedQueueDTO> queueDTOList) {
        //TODO: 추후 알림 기능 추가할 때 모든 메시지 처리에 대한 알림 발송하도록 개선.
        queueDTOList.forEach(this::retryMessages);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param dto
     *
     * 해당 DLQ 메시지를 재시도
     */
    private void retryMessages(FailedQueueDTO dto) {
        for(int i = 0; i < dto.messageCount(); i++) {
            Message message = rabbitTemplate.receive(dto.queueName());
            if(message != null) {
                Object data = converter.fromMessage(message);
                Map<String, Object> headers = message.getMessageProperties().getHeaders();
                List<Map<String, Object>> xDeathList = (List<Map<String, Object>>) headers.get("x-death");

                if(xDeathList != null && !xDeathList.isEmpty()) {
                    Map<String, Object> xDeath = xDeathList.get(0);
                    String exchange = (String) xDeath.get("exchange");
                    List<String> routingKeyList = (List<String>) xDeath.get("routing-keys");
                    String routingKey = routingKeyList.get(0);
                    rabbitTemplate.convertAndSend(exchange, routingKey, data);
                }
            }
        }
    }

    /**
     * 주문 데이터 처리 중 문제가 발생해 Redis에 적재된 데이터 조회
     */
    @Override
    public long getFailedOrderDataByRedis() {

        Set<String> failedOrderKeys = getFailedOrderRedisKeys(FallbackMapKey.ORDER);
        Set<String> failedMessageKeys = getFailedOrderRedisKeys(FallbackMapKey.ORDER_MESSAGE);

        return failedOrderKeys.size() + failedMessageKeys.size();
    }

    /**
     *
     * @param fallbackMapKey
     *
     * 주문 실패 데이터 Redis Key 조회
     */
    private Set<String> getFailedOrderRedisKeys(FallbackMapKey fallbackMapKey) {
        String keyPrefix = fallbackProperties.getRedis().get(fallbackMapKey.getKey()).getPrefix();

        return failedOrderRedisTemplate.keys(keyPrefix + "*");
    }

    /**
     *
     * Redis에 저장된 주문 실패 데이터 재처리
     */
    @Override
    public String retryFailedOrderDataByRedis() {
        Set<String> failedOrderKeys = getFailedOrderRedisKeys(FallbackMapKey.ORDER);
        Set<String> failedMessageKeys = getFailedOrderRedisKeys(FallbackMapKey.ORDER_MESSAGE);

        if(failedOrderKeys.isEmpty() && failedMessageKeys.isEmpty())
            return Result.EMPTY.getResultKey();

        if(!failedOrderKeys.isEmpty())
            retryFailedOrderData(failedOrderKeys, FallbackMapKey.ORDER);

        if(!failedMessageKeys.isEmpty())
            retryFailedOrderData(failedMessageKeys, FallbackMapKey.ORDER_MESSAGE);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param keys
     * @param fallbackMapKey
     *
     * Redis 데이터 조회 후 재처리 메서드 호출
     */
    private void retryFailedOrderData(Set<String> keys, FallbackMapKey fallbackMapKey) {
        List<String> keyList = keys.stream().toList();
        List<FailedOrderDTO> dataList = failedOrderRedisTemplate.opsForValue().multiGet(keyList);
        for(int i = 0; i < dataList.size(); i++) {
            FailedOrderDTO data = dataList.get(i);

            String response = orderService.retryFailedOrder(data, fallbackMapKey);

            if(response.equals(Result.OK.getResultKey()))
                failedOrderRedisTemplate.delete(keyList.get(i));
        }
    }

    /**
     * 실패 로그를 통한 로그 조회 및 재처리 메서드
     * 아직 보류.
     */
    public void retryFailedOrderDataByJSON() {
        //TODO: 관리자가 로그 확인해서 입력하면 해당 데이터를 FailedOrderDTO로 파싱해서 반환
        //TODO: 그럼 컨트롤러에서 orderService 재호출
    }
}
