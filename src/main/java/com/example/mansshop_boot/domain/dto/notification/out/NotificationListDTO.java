package com.example.mansshop_boot.domain.dto.notification.out;

public record NotificationListDTO(
	String title,
	Long relatedId,
	String type
) {

	public NotificationListDTO(String title, Long relatedId, String type) {
		this.title = title;
		this.relatedId = relatedId == null ? 0L : relatedId;
		this.type = type;
	}
}
