package com.example.mansshop_boot.domain.dto.notification.business;

import com.example.mansshop_boot.domain.enumeration.NotificationType;

public record NotificationSendDTO(
	String userId,
	NotificationType type,
	String title,
	Long relatedId
) {
}
