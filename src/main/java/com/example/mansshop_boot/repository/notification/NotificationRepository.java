package com.example.mansshop_boot.repository.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mansshop_boot.domain.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationDSLRepository{
	
}
