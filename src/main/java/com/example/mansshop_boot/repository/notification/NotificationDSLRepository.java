package com.example.mansshop_boot.repository.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.mansshop_boot.domain.dto.notification.out.NotificationListDTO;

public interface NotificationDSLRepository {
	Page<NotificationListDTO> findAllByUserId(String userId, Pageable pageable);

	void updateIsRead(String userId);
}
