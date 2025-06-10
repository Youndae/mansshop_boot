package com.example.mansshop_boot.service.admin;

import com.example.mansshop_boot.domain.dto.rabbitMQ.FailedQueueDTO;

import java.util.List;

public interface AdminFailedDataService {

    List<FailedQueueDTO> getFailedMessageList();

    String retryFailedMessages(List<FailedQueueDTO> queueDTOList);

    long getFailedOrderDataByRedis();

    String retryFailedOrderDataByRedis();
}
