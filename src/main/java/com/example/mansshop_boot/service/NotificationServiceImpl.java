package com.example.mansshop_boot.service;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.domain.dto.mypage.business.MyPagePageDTO;
import com.example.mansshop_boot.domain.dto.notification.business.NotificationSendDTO;
import com.example.mansshop_boot.domain.dto.notification.out.NotificationDTO;
import com.example.mansshop_boot.domain.dto.notification.out.NotificationListDTO;
import com.example.mansshop_boot.domain.dto.pageable.PagingMappingDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.entity.Notification;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.notification.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

	private final MemberRepository memberRepository;

	private final NotificationRepository notificationRepository;

	private final SimpMessagingTemplate messageTemplate;

	private final PrincipalService principalService;

	private final RedisTemplate<String, String> redisTemplate;

	@Value("${notification.redis.prefix}")
	private String redisPrefix;
	
	@Value("${notification.redis.ttl}")
	private Long redisTtl;

	@Value("${notification.redis.status}")
	private String redisStatus;

	@Override
	public void sendNotification(NotificationSendDTO sendDTO) {
		
		Member member = memberRepository.findByUserId(sendDTO.userId());

		if(member == null){
			log.warn("Notification user Not Found");
			throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());
		}


		Notification notification = Notification.builder()
		.member(member)
		.type(sendDTO.type().getType())
		.title(sendDTO.title())
		.relatedId(sendDTO.relatedId())
		.isRead(false)
		.build();

		notificationRepository.save(notification);

		NotificationDTO responseMessage = new NotificationDTO(sendDTO.title(), sendDTO.relatedId());

		if(isUserOnline(sendDTO.userId()))
			messageTemplate.convertAndSendToUser(sendDTO.userId(), "/queue/notifications", responseMessage);

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public PagingListDTO<NotificationListDTO> getNotificationList(MyPagePageDTO pageDTO, Principal principal) {
		String userId = principalService.getUserIdByPrincipal(principal);
		Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1, 
											pageDTO.amount(), 
											Sort.by("createdAt").descending()
										);

		Page<NotificationListDTO> notifications = notificationRepository.findAllByUserId(userId, pageable);

		PagingMappingDTO pagingMappingDTO = PagingMappingDTO.builder()
													.empty(notifications.isEmpty())
													.number(notifications.getNumber())
													.totalPages(notifications.getTotalPages())
													.totalElements(notifications.getTotalElements())
													.build();

		// 알림 조회하는 순간 모든 알림을 읽음 처리
		if(!notifications.isEmpty())
			notificationRepository.updateIsRead(userId);
		
		return new PagingListDTO<>(notifications.getContent(), pagingMappingDTO);
	}

	@Override
	public void updateUserOnlineStatus(Principal principal) {
		String userId = principalService.getUserIdByPrincipal(principal);
		String key = redisPrefix + userId;

		redisTemplate.opsForValue().set(key, redisStatus, redisTtl, TimeUnit.SECONDS);
	}

	@Override
	public boolean isUserOnline(String userId) {
		String key = redisPrefix + userId;
		String status = redisTemplate.opsForValue().get(key);

		return status != null && status.equals(redisStatus);
	}
}
