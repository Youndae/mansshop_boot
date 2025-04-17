package com.example.mansshop_boot.domain.dto.rabbitMQ;

import io.swagger.v3.oas.annotations.media.Schema;

public record FailedQueueDTO(
        @Schema(name = "queueName", description = "DLQ 이름")
        String queueName,
        @Schema(name = "messageCount", description = "실패한 메시지 개수")
        int messageCount
) {
}
