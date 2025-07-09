package com.example.mansshop_boot.service;

import java.security.Principal;

import com.example.mansshop_boot.domain.dto.mypage.business.MyPagePageDTO;
import com.example.mansshop_boot.domain.dto.notification.business.NotificationSendDTO;
import com.example.mansshop_boot.domain.dto.notification.out.NotificationListDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;

public interface NotificationService {
	void sendNotification(NotificationSendDTO sendDTO);

	PagingListDTO<NotificationListDTO> getNotificationList(MyPagePageDTO pageDTO, Principal principal);

	void updateUserOnlineStatus(Principal principal);

	boolean isUserOnline(String userId);
}
