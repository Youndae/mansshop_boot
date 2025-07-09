package com.example.mansshop_boot.config.rabbitMQ.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.mansshop_boot.domain.dto.notification.business.NotificationSendDTO;
import com.example.mansshop_boot.service.NotificationService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificationConsumer {
	
	private final NotificationService notificationService;

	public NotificationConsumer(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@RabbitListener(queues = "${rabbitmq.queue.notificationSend.name}", concurrency = "3")
	public void consumeNotification(NotificationSendDTO notificationDTO) {
		notificationService.sendNotification(notificationDTO);
	}
}
