package com.example.mansshop_boot.service.admin;

import com.example.mansshop_boot.domain.dto.admin.out.AdminQnAClassificationDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminQnAListResponseDTO;
import com.example.mansshop_boot.domain.dto.cache.CacheRequest;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyInsertDTO;
import com.example.mansshop_boot.domain.dto.notification.business.NotificationSendDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.PagingMappingDTO;
import com.example.mansshop_boot.domain.dto.rabbitMQ.RabbitMQProperties;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.NotificationType;
import com.example.mansshop_boot.domain.enumeration.RabbitMQPrefix;
import com.example.mansshop_boot.domain.enumeration.RedisCaching;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.memberQnA.MemberQnARepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnAReplyRepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnARepository;
import com.example.mansshop_boot.repository.qnaClassification.QnAClassificationRepository;
import com.example.mansshop_boot.service.MyPageService;
import com.example.mansshop_boot.service.PrincipalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminQnAServiceImpl implements AdminQnAService {

    private final AdminCacheService adminCacheService;

    private final PrincipalService principalService;

    private final MyPageService myPageService;

    private final ProductQnARepository productQnARepository;

    private final ProductQnAReplyRepository productQnAReplyRepository;

    private final MemberRepository memberRepository;

    private final MemberQnARepository memberQnARepository;

    private final QnAClassificationRepository qnAClassificationRepository;

	private final RabbitTemplate rabbitTemplate;

	private final RabbitMQProperties rabbitMQProperties;

    /**
     *
     * @param pageDTO
     *
     * 상품 문의 목록 조회
     * pageDTO의 SearchType은 new, all 두가지로 구분.
     * 미처리와 전체 목록을 의미. 그래서 쿼리에서는 이 SearchType에 따라 조회.
     * 검색은 nickname OR userId로 처리. 이때, LIKE 가 아닌 equals로 처리.
     */
    @Override
    public PagingListDTO<AdminQnAListResponseDTO> getProductQnAList(AdminOrderPageDTO pageDTO) {
        List<AdminQnAListResponseDTO> responseDTO = productQnARepository.findAllByAdminProductQnA(pageDTO);
        Long totalElements = 0L;

        if(!responseDTO.isEmpty()) {
            if(pageDTO.searchType().equals("all") && pageDTO.keyword() == null)
                totalElements = adminCacheService.getFullScanCountCache(RedisCaching.ADMIN_PRODUCT_QNA_COUNT, new CacheRequest(pageDTO));
            else
                totalElements = productQnARepository.findAllByAdminProductQnACount(pageDTO);
        }

        PagingMappingDTO pagingMappingDTO = new PagingMappingDTO(totalElements, pageDTO.page(), pageDTO.amount());

        return new PagingListDTO<>(responseDTO, pagingMappingDTO);
    }

    /**
     *
     * @param qnaId
     *
     * 상품 문의 답변 완료 처리.
     * 다른 처리 없이 답변 상태만 완료 상태로 수정.
     */
    @Override
    public String patchProductQnAComplete(long qnaId) {
        ProductQnA productQnA = productQnARepository.findById(qnaId).orElseThrow(IllegalArgumentException::new);
        patchProductQnAStatus(productQnA);

        return Result.OK.getResultKey();
    }

    private void patchProductQnAStatus(ProductQnA productQnA) {
        productQnA.setProductQnAStat(true);
        productQnARepository.save(productQnA);
    }

    /**
     *
     * @param insertDTO
     * @param principal
     *
     * 상품 답변 작성 처리.
     * ProductQnAReply에 save 처리 후 상태값 변경을 위해 patchProductQnAComplete 메소드를 호출해 수정한다.
     */
    @Override
    public String postProductQnAReply(QnAReplyInsertDTO insertDTO, Principal principal) {
        Member member = memberRepository.findById(principal.getName()).orElseThrow(IllegalArgumentException::new);
        ProductQnA productQnA = productQnARepository.findById(insertDTO.qnaId()).orElseThrow(IllegalArgumentException::new);

        ProductQnAReply productQnAReply = ProductQnAReply.builder()
                .member(member)
                .productQnA(productQnA)
                .replyContent(insertDTO.content())
                .build();

        productQnAReplyRepository.save(productQnAReply);
        patchProductQnAStatus(productQnA);

		String notifictionTitle = productQnA.getProduct().getProductName() + NotificationType.PRODUCT_QNA_REPLY.getTitle();

		rabbitTemplate.convertAndSend(
			rabbitMQProperties.getExchange().get(RabbitMQPrefix.EXCHANGE_NOTIFICATION.getKey()).getName(),
			rabbitMQProperties.getQueue().get(RabbitMQPrefix.QUEUE_NOTIFICATION.getKey()).getRouting(),
			new NotificationSendDTO(
				productQnA.getMember().getUserId(), 
				NotificationType.PRODUCT_QNA_REPLY, 
				notifictionTitle, 
				productQnA.getId()
			)
		);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param replyDTO
     * @param principal
     *
     * 상품문의 답변 수정 처리.
     */
    @Override
    public String patchProductQnAReply(QnAReplyDTO replyDTO, Principal principal) {
        ProductQnAReply productQnAReply = productQnAReplyRepository.findById(replyDTO.replyId()).orElseThrow(IllegalArgumentException::new);
        String userId = principalService.getUserIdByPrincipal(principal);

        if(!productQnAReply.getMember().getUserId().equals(userId))
            throw new IllegalArgumentException();

        productQnAReply.setReplyContent(replyDTO.content());
        productQnAReplyRepository.save(productQnAReply);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param pageDTO
     *
     * 회원 문의 목록 조회.
     * 상품 문의와 마찬가지로 pageDTO의 SearchType은 new, all 두가지로 구분.
     * 미처리와 전체 목록을 의미. 그래서 쿼리에서는 이 SearchType에 따라 조회.
     * 검색은 nickname OR userId로 처리. 이때 LIKE가 아닌 equals로 처리.
     */
    @Override
    public PagingListDTO<AdminQnAListResponseDTO> getMemberQnAList(AdminOrderPageDTO pageDTO) {
        List<AdminQnAListResponseDTO> responseDTO = memberQnARepository.findAllByAdminMemberQnA(pageDTO);
        Long totalElements = 0L;

        if(!responseDTO.isEmpty()) {
            if(pageDTO.searchType().equals("all") && pageDTO.keyword() == null)
                totalElements = adminCacheService.getFullScanCountCache(RedisCaching.ADMIN_MEMBER_QNA_COUNT, new CacheRequest(pageDTO));
            else
                totalElements = memberQnARepository.findAllByAdminMemberQnACount(pageDTO);
        }

        PagingMappingDTO pagingMappingDTO = new PagingMappingDTO(totalElements, pageDTO.page(), pageDTO.amount());

        return new PagingListDTO<>(responseDTO, pagingMappingDTO);
    }

    /**
     *
     * @param qnaId
     *
     * 회원 문의 답변 완료 처리
     */
    @Override
    public String patchMemberQnAComplete(long qnaId) {
        MemberQnA memberQnA = memberQnARepository.findById(qnaId).orElseThrow(IllegalArgumentException::new);
        
        return patchMemberQnAStatus(memberQnA);
    }

	private String patchMemberQnAStatus(MemberQnA memberQnA) {
		memberQnA.setMemberQnAStat(true);
		memberQnARepository.save(memberQnA);

		return Result.OK.getResultKey();
	}

    /**
     *
     * @param insertDTO
     * @param principal
     *
     * 회원 문의 답변 작성
     */
    @Override
    public String postMemberQnAReply(QnAReplyInsertDTO insertDTO, Principal principal) {
        String postReplyResult = myPageService.postMemberQnAReply(insertDTO, principal);

        if(postReplyResult.equals(Result.OK.getResultKey())){
			MemberQnA memberQnA = memberQnARepository.findById(insertDTO.qnaId()).orElseThrow(IllegalArgumentException::new);
			String response = patchMemberQnAStatus(memberQnA);
			String notifictionTitle = memberQnA.getMemberQnATitle() + NotificationType.MEMBER_QNA_REPLY.getTitle();
			rabbitTemplate.convertAndSend(
				rabbitMQProperties.getExchange().get(RabbitMQPrefix.EXCHANGE_NOTIFICATION.getKey()).getName(),
				rabbitMQProperties.getQueue().get(RabbitMQPrefix.QUEUE_NOTIFICATION.getKey()).getRouting(),
				new NotificationSendDTO(
					memberQnA.getMember().getUserId(), 
					NotificationType.MEMBER_QNA_REPLY, 
					notifictionTitle, 
					insertDTO.qnaId()
				)
			);

            return response;
        }else {
            throw new IllegalArgumentException();
        }

    }

    /**
     *
     * 문의 분류 리스트 조회
     * Entity를 그대로 반환하지 않도록 하기 위해 DTO에 매핑 후 반환.
     */
    @Override
    public List<AdminQnAClassificationDTO> getQnAClassification() {
        List<QnAClassification> entity = qnAClassificationRepository.findAll(Sort.by("id").ascending());

        return entity.stream()
                .map(AdminQnAClassificationDTO::new)
                .toList();
    }

    /**
     *
     * @param classificationName
     *
     * 문의 분류 추가 처리
     */
    @Override
    public String postQnAClassification(String classificationName) {
        QnAClassification entity = QnAClassification.builder()
                .qnaClassificationName(classificationName)
                .build();

        qnAClassificationRepository.save(entity);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param classificationId
     *
     * 문의 분류 삭제 처리
     */
    @Override
    public String deleteQnAClassification(long classificationId) {
        qnAClassificationRepository.findById(classificationId).orElseThrow(IllegalArgumentException::new);
        qnAClassificationRepository.deleteById(classificationId);

        return Result.OK.getResultKey();
    }
}
