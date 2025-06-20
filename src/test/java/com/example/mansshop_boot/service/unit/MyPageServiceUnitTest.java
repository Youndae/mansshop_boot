package com.example.mansshop_boot.service.unit;

import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.config.customException.exception.CustomNotFoundException;
import com.example.mansshop_boot.domain.dto.mypage.business.MemberOrderDTO;
import com.example.mansshop_boot.domain.dto.mypage.business.MyPageOrderDetailDTO;
import com.example.mansshop_boot.domain.dto.mypage.business.MyPagePageDTO;
import com.example.mansshop_boot.domain.dto.mypage.in.MyPagePatchReviewDTO;
import com.example.mansshop_boot.domain.dto.mypage.in.MyPagePostReviewDTO;
import com.example.mansshop_boot.domain.dto.mypage.out.*;
import com.example.mansshop_boot.domain.dto.mypage.qna.business.MemberQnADTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.business.MyPageProductQnADTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.business.MyPageQnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.MemberQnAModifyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.out.*;
import com.example.mansshop_boot.domain.dto.pageable.LikePageDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.memberQnA.MemberQnAReplyRepository;
import com.example.mansshop_boot.repository.memberQnA.MemberQnARepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.productLike.ProductLikeRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderDetailRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderRepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnAReplyRepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnARepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewRepository;
import com.example.mansshop_boot.repository.qnaClassification.QnAClassificationRepository;
import com.example.mansshop_boot.service.MyPageServiceImpl;
import com.example.mansshop_boot.service.PrincipalService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MyPageServiceUnitTest {

    @InjectMocks
    private MyPageServiceImpl myPageService;

    @Mock
    private ProductOrderRepository productOrderRepository;

    @Mock
    private ProductOrderDetailRepository productOrderDetailRepository;

    @Mock
    private ProductLikeRepository productLikeRepository;

    @Mock
    private ProductQnARepository productQnARepository;

    @Mock
    private PrincipalService principalService;

    @Mock
    private ProductQnAReplyRepository productQnAReplyRepository;

    @Mock
    private MemberQnARepository memberQnARepository;

    @Mock
    private MemberQnAReplyRepository memberQnAReplyRepository;

    @Mock
    private QnAClassificationRepository qnAClassificationRepository;

    @Mock
    private ProductReviewRepository productReviewRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductOptionRepository productOptionRepository;

    private Principal principal;

    @BeforeEach
    void init() {
        principal = Mockito.mock(Principal.class);
    }


    @Test
    @DisplayName(value = "주문 목록 조회")
    void getOrderList() {
        OrderPageDTO pageDTO = new OrderPageDTO(1, "3");
        MemberOrderDTO memberOrderDTO = new MemberOrderDTO("testUser", null, null);
        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.orderAmount()
                , Sort.by("orderId").descending());

        ProductOrder order1 = ProductOrder.builder()
                .id(1L)
                .member(Member.builder().userId("testUser").build())
                .orderTotalPrice(10000)
                .createdAt(LocalDateTime.now())
                .orderStat("배송 완료")
                .build();

        ProductOrder order2 = ProductOrder.builder()
                .id(2L)
                .member(Member.builder().userId("testUser").build())
                .orderTotalPrice(20000)
                .createdAt(LocalDateTime.now())
                .orderStat("배송 준비중")
                .build();

        List<ProductOrder> orders = List.of(order1, order2);
        Page<ProductOrder> orderResult = new PageImpl<>(orders, pageable, 2L);
        List<Long> orderIds = orderResult.getContent().stream().map(ProductOrder::getId).toList();

        List<MyPageOrderDetailDTO> detailDTOResult = IntStream.range(1, 3)
                                                            .mapToObj(v ->
                                                                    new MyPageOrderDetailDTO(
                                                                            v,
                                                                            "testProductId" + v,
                                                                            v,
                                                                            v,
                                                                            "testProductName" + v,
                                                                            "testSize" + v,
                                                                            "testColor" + v,
                                                                            1,
                                                                            v * 10000,
                                                                            v % 2 == 0,
                                                                            "testThumbnail" + v
                                                                    )
                                                            )
                                                            .toList();

        when(productOrderRepository.findByUserId(memberOrderDTO, pageDTO, pageable))
                .thenReturn(orderResult);
        when(productOrderDetailRepository.findByDetailList(orderIds)).thenReturn(detailDTOResult);

        PagingListDTO<MyPageOrderDTO> result = assertDoesNotThrow(() -> myPageService.getOrderList(pageDTO, memberOrderDTO));

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals(orderResult.getContent().size(), result.content().size());
        assertEquals(orderResult.getContent().size(), result.pagingData().getTotalElements());
        assertEquals(1, result.pagingData().getTotalPages());
        assertEquals(1, result.content().get(0).detail().size());
    }

    @Test
    @DisplayName(value = "주문 목록 조회. 데이터가 없는 경우")
    void getOrderListIsEmpty() {
        OrderPageDTO pageDTO = new OrderPageDTO(1, "3");
        MemberOrderDTO memberOrderDTO = new MemberOrderDTO("testUser", null, null);
        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.orderAmount()
                , Sort.by("orderId").descending());

        when(productOrderRepository.findByUserId(memberOrderDTO, pageDTO, pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0L));

        PagingListDTO<MyPageOrderDTO> result = assertDoesNotThrow(() -> myPageService.getOrderList(pageDTO, memberOrderDTO));

        assertTrue(result.content().isEmpty());
        assertEquals(0, result.pagingData().getTotalElements());
        assertEquals(0, result.pagingData().getTotalPages());
        assertTrue(result.pagingData().isEmpty());
    }

    @Test
    @DisplayName(value = "관심상품 목록 조회")
    void getLikeList() {
        LikePageDTO pageDTO = new LikePageDTO(1);
        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.likeAmount()
                , Sort.by("createdAt").descending());
        List<ProductLikeDTO> dtoList = IntStream.range(1, 3)
                .mapToObj(v ->
                        new ProductLikeDTO(
                                (long) v,
                                "testProductId" + v,
                                "testProductName" + v,
                                5000,
                                "testProductThumbnail" + v,
                                5,
                                LocalDate.now()
                        )
                )
                .toList();

        when(principal.getName()).thenReturn("testUser");
        when(productLikeRepository.findByUserId("testUser", pageable))
                .thenReturn(new PageImpl<>(dtoList, pageable, 2L));


        Page<ProductLikeDTO> result = assertDoesNotThrow(() -> myPageService.getLikeList(pageDTO, principal));

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(dtoList.size(), result.getTotalElements());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    @DisplayName(value = "관심상품 목록 조회. Principal 에서 예외가 발생하는 경우")
    void getLikeListAccessDenied() {
        LikePageDTO pageDTO = new LikePageDTO(1);

        assertThrows(CustomAccessDeniedException.class,
                                () -> myPageService.getLikeList(pageDTO, null)
        );
    }

    @Test
    @DisplayName(value = "상품 문의 목록 조회")
    void getProductQnAList() {
        MyPagePageDTO pageDTO = new MyPagePageDTO(1);
        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.amount()
                , Sort.by("id").descending());

        ProductQnAListDTO dto = new ProductQnAListDTO(
                1L,
                "testProductName",
                false,
                LocalDate.now()
        );

        when(principalService.getUserIdByPrincipal(principal)).thenReturn("testUser");
        when(productQnARepository.findByUserId("testUser", pageable))
                .thenReturn(new PageImpl<>(List.of(dto), pageable, 1L));

        Page<ProductQnAListDTO> result = assertDoesNotThrow(() -> myPageService.getProductQnAList(pageDTO, principal));

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName(value = "상품 문의 상세 데이터 조회")
    void getProductQnADetail() {
        long productQnAId = 1L;
        MyPageProductQnADTO dto = new MyPageProductQnADTO(
                productQnAId,
                "productName1",
                "testNickname",
                "testQnAContent",
                LocalDateTime.now(),
                true
        );
        MyPageQnAReplyDTO reply1 = new MyPageQnAReplyDTO(
                1L,
                "admin",
                "testReplyContent",
                LocalDate.now()
        );
        List<MyPageQnAReplyDTO> replyList = List.of(reply1);
        ProductQnADetailDTO resultDTO = new ProductQnADetailDTO(dto, replyList);

        when(principalService.getNicknameByPrincipal(principal)).thenReturn("testNickname");
        when(productQnARepository.findByQnAId(productQnAId)).thenReturn(dto);
        when(productQnAReplyRepository.findAllByQnAId(productQnAId)).thenReturn(replyList);

        ProductQnADetailDTO result = assertDoesNotThrow(() -> myPageService.getProductQnADetail(productQnAId, principal));

        assertNotNull(result);
        assertEquals(resultDTO.productQnAId(), result.productQnAId());
        assertEquals(resultDTO.writer(), result.writer());
    }

    @Test
    @DisplayName(value = "상품 문의 상세 데이터 조회. 작성자가 일치하지 않는 경우")
    void getProductQnADetailAccessDenied() {
        long productQnAId = 1L;
        MyPageProductQnADTO dto = new MyPageProductQnADTO(
                productQnAId,
                "productName1",
                "testUser",
                "testQnAContent",
                LocalDateTime.now(),
                true
        );
        MyPageQnAReplyDTO reply1 = new MyPageQnAReplyDTO(
                1L,
                "admin",
                "testReplyContent",
                LocalDate.now()
        );
        List<MyPageQnAReplyDTO> replyList = List.of(reply1);

        when(principalService.getNicknameByPrincipal(principal)).thenReturn("testNickname");
        when(productQnARepository.findByQnAId(productQnAId)).thenReturn(dto);
        when(productQnAReplyRepository.findAllByQnAId(productQnAId)).thenReturn(replyList);

        assertThrows(CustomAccessDeniedException.class,
                () -> myPageService.getProductQnADetail(productQnAId, principal)
        );
    }

    @Test
    @DisplayName(value = "상품 문의 상세 데이터 조회. 문의 데이터가 없는 경우")
    void getProductQnADetailNotFound() {
        long productQnAId = 1L;

        when(principalService.getNicknameByPrincipal(principal)).thenReturn("testNickname");
        when(productQnARepository.findByQnAId(productQnAId)).thenReturn(null);

        assertThrows(CustomNotFoundException.class,
                () -> myPageService.getProductQnADetail(productQnAId, principal)
        );

        verify(productQnAReplyRepository, never()).findAllByQnAId(productQnAId);
    }

    @Test
    @DisplayName(value = "상품 문의 제거")
    void deleteProductQnA() {
        ProductQnA deleteEntity = ProductQnA.builder()
                .id(1L)
                .member(Member.builder().userId("testUser").build())
                .build();

        when(principalService.getUserIdByPrincipal(principal)).thenReturn(deleteEntity.getMember().getUserId());
        when(productQnARepository.findById(deleteEntity.getId())).thenReturn(Optional.of(deleteEntity));
        doNothing().when(productQnARepository).deleteById(deleteEntity.getId());

        String result = assertDoesNotThrow(() -> myPageService.deleteProductQnA(deleteEntity.getId(), principal));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);
    }

    @Test
    @DisplayName(value = "상품 문의 제거. 작성자가 일치하지 않는 경우")
    void deleteProductQnAWriterIsNotEquals() {
        ProductQnA deleteEntity = ProductQnA.builder()
                .id(1L)
                .member(Member.builder().userId("testUser").build())
                .build();

        when(principalService.getUserIdByPrincipal(principal)).thenReturn("Anonymous");
        when(productQnARepository.findById(deleteEntity.getId())).thenReturn(Optional.of(deleteEntity));

        assertThrows(CustomAccessDeniedException.class, () -> myPageService.deleteProductQnA(deleteEntity.getId(), principal));
        verify(productQnARepository, never()).deleteById(1L);
    }

    @Test
    @DisplayName(value = "상품 문의 제거. 데이터가 존재하지 않는 경우")
    void deleteProductQnANotFoundData() {

        when(principalService.getUserIdByPrincipal(principal)).thenReturn("testUser");
        when(productQnARepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> myPageService.deleteProductQnA(1L, principal));
        verify(productQnARepository, never()).deleteById(1L);
    }

    @Test
    @DisplayName(value = "회원 문의 목록 조회.")
    void getMemberQnAList() {
        MyPagePageDTO pageDTO = new MyPagePageDTO(1);
        String userId = "testUser";
        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.amount()
                , Sort.by("id").descending());
        MemberQnAListDTO dto = new MemberQnAListDTO(
                1L,
                "testMemberQnATitle",
                true,
                "testMemberQnAClassificationName",
                LocalDate.now()
        );
        List<MemberQnAListDTO> memberQnAList = List.of(dto);

        when(principalService.getUserIdByPrincipal(principal)).thenReturn(userId);
        when(memberQnARepository.findAllByUserId(userId, pageable)).thenReturn(new PageImpl<>(memberQnAList, pageable, 1L));

        Page<MemberQnAListDTO> result = assertDoesNotThrow(() -> myPageService.getMemberQnAList(pageDTO, principal));

        assertNotNull(result);
        assertEquals(memberQnAList.size(), result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(dto.memberQnAId(), result.getContent().get(0).memberQnAId());
        assertEquals(dto.memberQnATitle(), result.getContent().get(0).memberQnATitle());
    }

    @Test
    @DisplayName(value = "회원 문의 목록 조회. 데이터가 없는 경우")
    void getMemberQnAListIsEmpty() {
        MyPagePageDTO pageDTO = new MyPagePageDTO(1);
        String userId = "testUser";
        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.amount()
                , Sort.by("id").descending());

        when(principalService.getUserIdByPrincipal(principal)).thenReturn(userId);
        when(memberQnARepository.findAllByUserId(userId, pageable)).thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0L));

        Page<MemberQnAListDTO> result = assertDoesNotThrow(() -> myPageService.getMemberQnAList(pageDTO, principal));

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName(value = "회원 문의 상세 조회")
    void getMemberQnADetail() {
        MemberQnADTO dto = new MemberQnADTO(
                1L,
                "teestQnAClassificationName",
                "testQnATitle",
                "testUserNickname",
                "testQnAContent",
                LocalDateTime.now(),
                true
        );
        MyPageQnAReplyDTO replyDTO1 = new MyPageQnAReplyDTO(
                1L,
                "관리자",
                "testAdmin's ReplyContent",
                LocalDate.now()
        );
        MyPageQnAReplyDTO replyDTO2 = new MyPageQnAReplyDTO(
                2L,
                "testUserNickname",
                "testUser's ReplyContent",
                LocalDate.now()
        );
        List<MyPageQnAReplyDTO> replyDTOList = List.of(replyDTO1, replyDTO2);


        when(principalService.getNicknameByPrincipal(principal)).thenReturn(dto.writer());
        when(memberQnARepository.findByQnAId(dto.memberQnAId())).thenReturn(dto);
        when(memberQnAReplyRepository.findAllByQnAId(dto.memberQnAId())).thenReturn(replyDTOList);

        MemberQnADetailDTO result = assertDoesNotThrow(() -> myPageService.getMemberQnADetail(dto.memberQnAId(), principal));

        assertNotNull(result);
        assertEquals(dto.memberQnAId(), result.memberQnAId());
        assertEquals(dto.writer(), result.writer());
        assertEquals(dto.updatedAt().toLocalDate(), result.updatedAt());
        assertEquals(dto.qnaContent(), result.qnaContent());
        assertEquals(dto.qnaTitle(), result.qnaTitle());
    }

    @Test
    @DisplayName(value = "회원 문의 상세 조회. 데이터가 없는 경우")
    void getMemberQnADetailNotFound() {

        when(principalService.getNicknameByPrincipal(principal)).thenReturn("testUser");
        when(memberQnARepository.findByQnAId(1L)).thenReturn(null);

        assertThrows(CustomNotFoundException.class, () -> myPageService.getMemberQnADetail(1L, principal));
    }

    @Test
    @DisplayName(value = "회원 문의 상세 조회. 작성자가 일치하지 않는 경우")
    void getMemberQnADetailWriterIsNotEquals() {
        MemberQnADTO dto = new MemberQnADTO(
                1L,
                "teestQnAClassificationName",
                "testQnATitle",
                "testUserNickname",
                "testQnAContent",
                LocalDateTime.now(),
                true
        );

        when(principalService.getNicknameByPrincipal(principal)).thenReturn("testUser");
        when(memberQnARepository.findByQnAId(1L)).thenReturn(dto);

        assertThrows(CustomAccessDeniedException.class, () -> myPageService.getMemberQnADetail(1L, principal));
    }

    @Test
    @DisplayName(value = "회원 문의 답변 수정. 작성자가 일치하지 않는 경우")
    void patchMemberQnAReplyWriterNotEquals() {
        QnAReplyDTO replyDTO = new QnAReplyDTO(1L, "testReply patch content");
        MemberQnAReply replyEntity = MemberQnAReply.builder()
                .id(1L)
                .member(Member.builder().userId("testUser").build())
                .build();

        when(principalService.getUserIdByPrincipal(principal)).thenReturn("Anonymous");
        when(memberQnAReplyRepository.findById(replyEntity.getId())).thenReturn(Optional.of(replyEntity));

        assertThrows(CustomAccessDeniedException.class, () -> myPageService.patchMemberQnAReply(replyDTO, principal));

        verify(memberQnAReplyRepository, never()).save(any(MemberQnAReply.class));
    }

    @Test
    @DisplayName(value = "회원 문의 답변 수정. 해당 데이터가 없는 경우")
    void patchMemberQnAReplyNotFound() {
        QnAReplyDTO replyDTO = new QnAReplyDTO(1L, "testReply patch content");

        when(memberQnAReplyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> myPageService.patchMemberQnAReply(replyDTO, principal));
        verify(memberQnAReplyRepository, never()).save(any(MemberQnAReply.class));
    }

    @Test
    @DisplayName(value = "회원 문의 수정을 위한 데이터 조회")
    void getModifyMemberQnAData() {
        List<QnAClassificationDTO> qnAClassificationList = IntStream.range(1, 4)
                .mapToObj(v ->
                        new QnAClassificationDTO(v, "testQnAClassificationName" + v)
                )
                .toList();
        MemberQnA entity = MemberQnA.builder()
                .id(1L)
                .memberQnATitle("testTitle")
                .memberQnAContent("testContent")
                .qnAClassification(QnAClassification.builder().id(1L).build())
                .build();

        when(principalService.getUserIdByPrincipal(principal)).thenReturn("testUser");
        when(memberQnARepository.findModifyDataByIdAndUserId(entity.getId(), "testUser")).thenReturn(entity);
        when(qnAClassificationRepository.getAllQnAClassificationDTOs()).thenReturn(qnAClassificationList);

        MemberQnAModifyDataDTO result = assertDoesNotThrow(() -> myPageService.getModifyData(entity.getId(), principal));

        assertEquals(entity.getId(), result.qnaId());
        assertEquals(entity.getMemberQnATitle(), result.qnaTitle());
        assertEquals(entity.getMemberQnAContent(), result.qnaContent());
        assertEquals(entity.getQnAClassification().getId(), result.qnaClassificationId());
        assertEquals(qnAClassificationList.size(), result.classificationList().size());
    }

    @Test
    @DisplayName(value = "회원 문의 수정을 위한 데이터 조회. 데이터가 없는 경우")
    void getModifyMemberQnADataNotFound() {
        MemberQnA entity = MemberQnA.builder()
                .id(1L)
                .memberQnATitle("testTitle")
                .memberQnAContent("testContent")
                .qnAClassification(QnAClassification.builder().id(1L).build())
                .build();

        when(principalService.getUserIdByPrincipal(principal)).thenReturn("testUser");
        when(memberQnARepository.findModifyDataByIdAndUserId(entity.getId(), "testUser")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> myPageService.getModifyData(entity.getId(), principal));
        verify(qnAClassificationRepository, never()).getAllQnAClassificationDTOs();
    }

    @Test
    @DisplayName(value = "회원 문의 수정 요청. 작성자가 일치하지 않는 경우")
    void patchMemberQnAWriterNotEquals() {
        MemberQnA entity = MemberQnA.builder()
                .id(1L)
                .member(Member.builder().userId("testUser").build())
                .memberQnATitle("testTitle")
                .memberQnAContent("testContent")
                .qnAClassification(QnAClassification.builder().id(1L).build())
                .build();
        MemberQnAModifyDTO modifyDTO = new MemberQnAModifyDTO(
                entity.getId(),
                "testPatchTitle",
                "testPatchContent",
                1L
        );

        when(memberQnARepository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(principalService.getUserIdByPrincipal(principal)).thenReturn("Anonymous");

        assertThrows(CustomAccessDeniedException.class, () -> myPageService.patchMemberQnA(modifyDTO, principal));
        verify(qnAClassificationRepository, never()).findById(anyLong());
        verify(memberQnARepository, never()).save(any(MemberQnA.class));
    }

    @Test
    @DisplayName(value = "회원 문의 수정 요청. 데이터가 없는 경우")
    void patchMemberQnANotFound() {
        MemberQnAModifyDTO modifyDTO = new MemberQnAModifyDTO(
                1L,
                "testPatchTitle",
                "testPatchContent",
                1L
        );

        when(memberQnARepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> myPageService.patchMemberQnA(modifyDTO, principal));
        verify(principalService, never()).getUserIdByPrincipal(principal);
        verify(qnAClassificationRepository, never()).findById(anyLong());
        verify(memberQnARepository, never()).save(any(MemberQnA.class));
    }

    @Test
    @DisplayName(value = "회원 문의 수정 요청. QnAClassification 데이터가 없는 경우")
    void patchMemberQnAClassificationNotFound() {
        MemberQnA entity = MemberQnA.builder()
                .id(1L)
                .member(Member.builder().userId("testUser").build())
                .memberQnATitle("testTitle")
                .memberQnAContent("testContent")
                .qnAClassification(QnAClassification.builder().id(1L).build())
                .build();
        MemberQnAModifyDTO modifyDTO = new MemberQnAModifyDTO(
                entity.getId(),
                "testPatchTitle",
                "testPatchContent",
                1L
        );

        when(memberQnARepository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(principalService.getUserIdByPrincipal(principal)).thenReturn(entity.getMember().getUserId());
        when(qnAClassificationRepository.findById(modifyDTO.classificationId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> myPageService.patchMemberQnA(modifyDTO, principal));
        verify(memberQnARepository, never()).save(any(MemberQnA.class));
    }

    @Test
    @DisplayName(value = "회원 문의 삭제 요청. 데이터가 없는 경우")
    void deleteMemberNotFound() {

        when(principalService.getUserIdByPrincipal(principal)).thenReturn("testUser");
        when(memberQnARepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> myPageService.deleteMemberQnA(1L, principal));

        verify(memberQnARepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName(value = "회원 문의 삭제 요청. 작성자가 일치하지 않는 경우")
    void deleteMemberQnAWriterIsNotEquals() {
        MemberQnA entity = MemberQnA.builder()
                .id(1L)
                .member(Member.builder().userId("testUser").build())
                .memberQnATitle("testTitle")
                .memberQnAContent("testContent")
                .qnAClassification(QnAClassification.builder().id(1L).build())
                .build();

        when(principalService.getUserIdByPrincipal(principal)).thenReturn("Anonymous");
        when(memberQnARepository.findById(entity.getId())).thenReturn(Optional.of(entity));

        assertThrows(CustomAccessDeniedException.class, () -> myPageService.deleteMemberQnA(entity.getId(), principal));
        verify(memberQnARepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName(value = "작성한 리뷰 목록 조회")
    void getReview() {
        MyPagePageDTO pageDTO = new MyPagePageDTO(1);
        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.amount()
                , Sort.by("id").descending());
        List<MyPageReviewDTO> resultList = IntStream.range(0, 2)
                .mapToObj(v ->
                        new MyPageReviewDTO(
                                v,
                                "testThumbnail" + v,
                                "testProductName" + v,
                                "testReviewContent" + v,
                                LocalDate.now(),
                                LocalDate.now(),
                                "testReviewreplyContent" + v,
                                LocalDate.now()
                        )
                )
                .toList();

        when(principalService.getUserIdByPrincipal(principal)).thenReturn("testUser");
        when(productReviewRepository.findAllByUserId("testUser", pageable))
                .thenReturn(new PageImpl<>(resultList, pageable, resultList.size()));

        Page<MyPageReviewDTO> result = assertDoesNotThrow(() -> myPageService.getReview(pageDTO, principal));

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(resultList.size(), result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(resultList.size(), result.getContent().size());
    }

    @Test
    @DisplayName(value = "작성한 리뷰 목록 조회. 데이터가 없는 경우")
    void getReviewIsEmpty() {
        MyPagePageDTO pageDTO = new MyPagePageDTO(1);
        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.amount()
                , Sort.by("id").descending());

        when(principalService.getUserIdByPrincipal(principal)).thenReturn("testUser");
        when(productReviewRepository.findAllByUserId("testUser", pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0L));

        Page<MyPageReviewDTO> result = assertDoesNotThrow(() -> myPageService.getReview(pageDTO, principal));

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    @DisplayName(value = "수정할 리뷰 데이터 조회")
    void getPatchReview() {
        ProductReview entity = ProductReview.builder()
                .id(1L)
                .member(Member.builder().userId("testUser").build())
                .reviewContent("testReviewContent")
                .product(Product.builder().productName("testProductName").build())
                .build();

        when(principalService.getUserIdByPrincipal(principal)).thenReturn("testUser");
        when(productReviewRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

        MyPagePatchReviewDataDTO result = assertDoesNotThrow(() -> myPageService.getPatchReview(entity.getId(), principal));

        assertNotNull(result);
        assertEquals(entity.getReviewContent(), result.content());
        assertEquals(entity.getProduct().getProductName(), result.productName());
    }

    @Test
    @DisplayName(value = "수정할 리뷰 데이터 조회. 데이터가 없는 경우")
    void getPatchReviewNotFound() {

        when(principalService.getUserIdByPrincipal(principal)).thenReturn("testUser");
        when(productReviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> myPageService.getPatchReview(1L, principal));
    }

    @Test
    @DisplayName(value = "수정할 리뷰 데이터 조회. 작성자가 일치하지 않는 경우")
    void getPatchReviewWriterIsNotEquals() {
        ProductReview entity = ProductReview.builder()
                .id(1L)
                .member(Member.builder().userId("testUser").build())
                .reviewContent("testReviewContent")
                .product(Product.builder().productName("testProductName").build())
                .build();

        when(principalService.getUserIdByPrincipal(principal)).thenReturn("Anonymous");
        when(productReviewRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

        assertThrows(CustomAccessDeniedException.class, () -> myPageService.getPatchReview(entity.getId(), principal));
    }

    @Test
    @DisplayName(value = "리뷰 작성 요청. 사용자 데이터가 없는 경우")
    void postReviewWriterNotFound() {
        MyPagePostReviewDTO reviewDTO = new MyPagePostReviewDTO(
                "testProductId",
                "testReviewContent",
                1L,
                1L
        );

        when(principal.getName()).thenReturn("testUser");
        when(memberRepository.findById("testUser")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> myPageService.postReview(reviewDTO, principal));

        verify(productRepository, never()).findById(reviewDTO.productId());
        verify(productOptionRepository, never()).findById(reviewDTO.optionId());
        verify(productOrderDetailRepository, never()).findById(reviewDTO.detailId());
        verify(productReviewRepository, never()).save(any(ProductReview.class));
        verify(productOrderDetailRepository, never()).save(any(ProductOrderDetail.class));
    }

    @Test
    @DisplayName(value = "리뷰 작성 요청. 상품 데이터가 없는 경우")
    void postReviewProductNotFound() {
        MyPagePostReviewDTO reviewDTO = new MyPagePostReviewDTO(
                "testProductId",
                "testReviewContent",
                1L,
                1L
        );
        Member member = Member.builder()
                        .userId("testUser")
                        .build();

        when(principal.getName()).thenReturn(member.getUserId());
        when(memberRepository.findById(member.getUserId())).thenReturn(Optional.of(member));
        when(productRepository.findById(reviewDTO.productId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> myPageService.postReview(reviewDTO, principal));

        verify(productOptionRepository, never()).findById(reviewDTO.optionId());
        verify(productOrderDetailRepository, never()).findById(reviewDTO.detailId());
        verify(productReviewRepository, never()).save(any(ProductReview.class));
        verify(productOrderDetailRepository, never()).save(any(ProductOrderDetail.class));
    }

    @Test
    @DisplayName(value = "리뷰 작성 요청. 상품 옵션 데이터가 없는 경우")
    void postReviewProductOptionNotFound() {
        MyPagePostReviewDTO reviewDTO = new MyPagePostReviewDTO(
                "testProductId",
                "testReviewContent",
                1L,
                1L
        );
        Member member = Member.builder()
                .userId("testUser")
                .build();
        Product product = Product.builder()
                        .id("testProductId")
                        .build();

        when(principal.getName()).thenReturn(member.getUserId());
        when(memberRepository.findById(member.getUserId())).thenReturn(Optional.of(member));
        when(productRepository.findById(reviewDTO.productId())).thenReturn(Optional.of(product));
        when(productOptionRepository.findById(reviewDTO.optionId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> myPageService.postReview(reviewDTO, principal));

        verify(productOrderDetailRepository, never()).findById(reviewDTO.detailId());
        verify(productReviewRepository, never()).save(any(ProductReview.class));
        verify(productOrderDetailRepository, never()).save(any(ProductOrderDetail.class));
    }

    @Test
    @DisplayName(value = "리뷰 작성 요청. 주문 상세 데이터가 없는 경우")
    void postReviewOrderDetailNotFound() {
        MyPagePostReviewDTO reviewDTO = new MyPagePostReviewDTO(
                "testProductId",
                "testReviewContent",
                1L,
                1L
        );
        Member member = Member.builder()
                .userId("testUser")
                .build();
        Product product = Product.builder()
                .id("testProductId")
                .build();
        ProductOption productOption = ProductOption.builder()
                        .id(1L)
                        .build();

        when(principal.getName()).thenReturn("testUser");
        when(memberRepository.findById(member.getUserId())).thenReturn(Optional.of(member));
        when(productRepository.findById(reviewDTO.productId())).thenReturn(Optional.of(product));
        when(productOptionRepository.findById(reviewDTO.optionId())).thenReturn(Optional.of(productOption));
        when(productOrderDetailRepository.findById(reviewDTO.detailId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> myPageService.postReview(reviewDTO, principal));

        verify(productReviewRepository, never()).save(any(ProductReview.class));
        verify(productOrderDetailRepository, never()).save(any(ProductOrderDetail.class));
    }

    @Test
    @DisplayName(value = "리뷰 수정 요청. 데이터가 없는 경우")
    void patchReviewNotFound() {
        MyPagePatchReviewDTO reviewDTO = new MyPagePatchReviewDTO(
                1L,
                "testPatchReviewContent"
        );

        when(productReviewRepository.findById(reviewDTO.reviewId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> myPageService.patchReview(reviewDTO, principal));

        verify(productReviewRepository, never()).save(any(ProductReview.class));
    }

    @Test
    @DisplayName(value = "리뷰 수정 요청. 작성자가 일치하지 않는 경우")
    void patchReviewWriterIsNotEquals() {
        MyPagePatchReviewDTO reviewDTO = new MyPagePatchReviewDTO(
                1L,
                "testPatchReviewContent"
        );
        ProductReview entity = ProductReview.builder()
                        .member(Member.builder().userId("testUser").build())
                        .build();

        when(principalService.getUserIdByPrincipal(principal)).thenReturn("Anonymous");
        when(productReviewRepository.findById(reviewDTO.reviewId())).thenReturn(Optional.of(entity));


        assertThrows(CustomAccessDeniedException.class, () -> myPageService.patchReview(reviewDTO, principal));

        verify(productReviewRepository, never()).save(any(ProductReview.class));
    }

    @Test
    @DisplayName(value = "리뷰 삭제 요청. 데이터가 없는 경우")
    void deleteReviewNotFound() {
        long reviewId = 1L;

        when(productReviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> myPageService.deleteReview(reviewId, principal));

        verify(principalService, never()).getUserIdByPrincipal(principal);
        verify(productReviewRepository, never()).deleteById(reviewId);
    }

    @Test
    @DisplayName(value = "리뷰 삭제 요청. 작성자가 일치하지 않는 경우")
    void deleteReviewWriterIsNotEquals() {
        ProductReview entity = ProductReview.builder()
                .id(1L)
                .member(Member.builder().userId("testUser").build())
                .build();

        when(productReviewRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(principalService.getUserIdByPrincipal(principal)).thenReturn("Anonymous");

        assertThrows(CustomAccessDeniedException.class, () -> myPageService.deleteReview(entity.getId(), principal));

        verify(productReviewRepository, never()).deleteById(entity.getId());
    }

    @Test
    @DisplayName(value = "정보 수정을 위한 사용자 정보 요청")
    void getMemberInfo() {
        Member member = Member.builder()
                .userId("testUser")
                .userEmail("testUser@testUser.com")
                .nickname("testNickname")
                .phone("010-1234-5678")
                .build();
        String[] splitMail = member.getUserEmail().split("@");
        String phone = member.getPhone().replaceAll("-", "");
        when(principal.getName()).thenReturn(member.getUserId());
        when(memberRepository.findById(member.getUserId())).thenReturn(Optional.of(member));

        MyPageInfoDTO result = assertDoesNotThrow(() -> myPageService.getInfo(principal));

        assertNotNull(result);
        assertEquals(member.getNickname(), result.nickname());
        assertEquals(phone, result.phone());
        assertEquals(splitMail[0], result.mailPrefix());
        assertEquals(splitMail[1], result.mailSuffix());
        assertEquals("none", result.mailType());
    }

    @Test
    @DisplayName(value = "정보 수정을 위한 사용자 정보 요청. 데이터가 없는 경우")
    void getMemberInfoNotFound() {

        when(principal.getName()).thenReturn("testUser");
        when(memberRepository.findById("testUser")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> myPageService.getInfo(principal));
    }

    @Test
    @DisplayName(value = "정보 수정 요청. 데이터가 없는 경우")
    void patchInfoNotFound() {

        when(principal.getName()).thenReturn("testUser");
        when(memberRepository.findById("testUser")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> myPageService.getInfo(principal));

        verify(memberRepository, never()).save(any(Member.class));
    }
}
