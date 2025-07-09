package com.example.mansshop_boot.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mansshop_boot.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_MEMBER')")
@RequestMapping("/api/notification")
public class NotificationController {

	private final NotificationService notificationService;
	

	/**
	 * 
	 * @param principal
	 * 
	 * 클라이언트 온라인 상태 갱신
	 * 주기적으로 클라이언트에서 요청을 보내고 Redis 데이터를 갱신하여 온라인 상태를 유지
	 */
	@Operation(hidden = true)
	@GetMapping("/heartbeat")
	public ResponseEntity<Void> checkHeartbeat(Principal principal) {
		notificationService.updateUserOnlineStatus(principal);

		return ResponseEntity.ok().build();
	}
	
}
