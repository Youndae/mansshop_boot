package com.example.mansshop_boot.service.unit.admin;

import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.domain.dto.admin.business.AdminReviewDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminReviewRequestDTO;
import com.example.mansshop_boot.domain.dto.cache.CacheRequest;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.entity.Product;
import com.example.mansshop_boot.domain.entity.ProductReview;
import com.example.mansshop_boot.domain.entity.ProductReviewReply;
import com.example.mansshop_boot.domain.enumeration.AdminListType;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewReplyRepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewRepository;
import com.example.mansshop_boot.service.admin.AdminCacheServiceImpl;
import com.example.mansshop_boot.service.admin.AdminReviewServiceImpl;
import com.example.mansshop_boot.service.unit.domain.ReviewAndReplyDTO;
import com.example.mansshop_boot.Fixture.AdminPageDTOFixture;
import com.example.mansshop_boot.service.unit.fixture.ProductReviewUnitFixture;
import com.example.mansshop_boot.service.unit.fixture.ProductUnitFixture;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminReviewServiceUnitTest {

    @InjectMocks
    private AdminReviewServiceImpl adminReviewService;

    @Mock
    private AdminCacheServiceImpl adminCacheService;

    @Mock
    private ProductReviewRepository productReviewRepository;

    @Mock
    private ProductReviewReplyRepository productReviewReplyRepository;

    @Mock
    private MemberRepository memberRepository;

    private List<ReviewAndReplyDTO> reviewAndReplyList;

    private Principal principal;

    private AdminOrderPageDTO adminOrderPageDTO;

    @BeforeAll
    void init() {
        List<Member> memberList = MemberAndAuthFixture.createDefaultMember(30).memberList();
        Product product = ProductUnitFixture.createSaveProductList(1, "OUTER").get(0);
        reviewAndReplyList = ProductReviewUnitFixture.createProductReviewList(memberList, product);

        adminOrderPageDTO = AdminPageDTOFixture.createDefaultAdminOrderPageDTO();
    }

    @BeforeEach
    void setUp() {
        principal = Mockito.mock(Principal.class);
    }

    private List<AdminReviewDTO> createAdminReviewResponseList() {
        return reviewAndReplyList.stream()
                                .map(v -> new AdminReviewDTO(
                                        v.productReview().getId(),
                                        v.productReview().getProduct().getProductName(),
                                        v.productReview().getMember().getUserId(),
                                        LocalDateTime.now(),
                                        v.productReview().isStatus()
                                ))
                                .limit(20)
                                .toList();
    }

    @Test
    @DisplayName(value = "전체 리뷰 리스트 조회")
    void getAllReviewList() {
        List<AdminReviewDTO> listResponse = createAdminReviewResponseList();

        when(productReviewRepository.findAllByAdminReviewList(adminOrderPageDTO, AdminListType.ALL.name()))
                .thenReturn(listResponse);
        when(adminCacheService.getFullScanCountCache(any(), any(CacheRequest.class)))
                .thenReturn((long) reviewAndReplyList.size());

        PagingListDTO<AdminReviewDTO> result = assertDoesNotThrow(() -> adminReviewService.getReviewList(adminOrderPageDTO, AdminListType.ALL));

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals(listResponse.size(), result.content().size());
        assertEquals(reviewAndReplyList.size(), result.pagingData().getTotalElements());
        assertEquals(2, result.pagingData().getTotalPages());
    }

    @Test
    @DisplayName(value = "전체 리뷰 리스트 조회. 데이터가 없는 경우")
    void getAllReviewListEmpty() {

        when(productReviewRepository.findAllByAdminReviewList(adminOrderPageDTO, AdminListType.ALL.name()))
                .thenReturn(Collections.emptyList());
        when(adminCacheService.getFullScanCountCache(any(), any(CacheRequest.class)))
                .thenReturn(0L);

        PagingListDTO<AdminReviewDTO> result = assertDoesNotThrow(() -> adminReviewService.getReviewList(adminOrderPageDTO, AdminListType.ALL));

        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertEquals(0, result.pagingData().getTotalElements());
        assertEquals(0, result.pagingData().getTotalPages());
    }

    @Test
    @DisplayName(value = "미답변 리뷰 리스트 조회")
    void getNewReviewList() {
        List<AdminReviewDTO> listResponse = createAdminReviewResponseList();

        when(productReviewRepository.findAllByAdminReviewList(adminOrderPageDTO, AdminListType.NEW.name()))
                .thenReturn(listResponse);
        when(productReviewRepository.countByAdminReviewList(adminOrderPageDTO, AdminListType.NEW.name()))
                .thenReturn((long) reviewAndReplyList.size());

        PagingListDTO<AdminReviewDTO> result = assertDoesNotThrow(() -> adminReviewService.getReviewList(adminOrderPageDTO, AdminListType.NEW));

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals(listResponse.size(), result.content().size());
        assertEquals(reviewAndReplyList.size(), result.pagingData().getTotalElements());
        assertEquals(2, result.pagingData().getTotalPages());
    }

    @Test
    @DisplayName(value = "미답변 리뷰 리스트 조회. 데이터가 없는 경우")
    void getNewReviewListEmpty() {

        when(productReviewRepository.findAllByAdminReviewList(adminOrderPageDTO, AdminListType.NEW.name()))
                .thenReturn(Collections.emptyList());
        when(productReviewRepository.countByAdminReviewList(adminOrderPageDTO, AdminListType.NEW.name()))
                .thenReturn(0L);

        PagingListDTO<AdminReviewDTO> result = assertDoesNotThrow(() -> adminReviewService.getReviewList(adminOrderPageDTO, AdminListType.NEW));

        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertEquals(0, result.pagingData().getTotalElements());
        assertEquals(0, result.pagingData().getTotalPages());
    }

    @Test
    @DisplayName(value = "리뷰 답변 작성. ProductReview 엔티티 조회 실패")
    void postReviewReplyNotFoundReply() {
        AdminReviewRequestDTO postDTO = new AdminReviewRequestDTO(1L, "testReviewReplyContent");
        Member admin = Member.builder().userId("admin").build();

        when(principal.getName()).thenReturn(admin.getUserId());
        when(memberRepository.findById("admin")).thenReturn(Optional.of(admin));
        when(productReviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> adminReviewService.postReviewReply(postDTO, principal)
        );

        verify(productReviewRepository, never()).save(any(ProductReview.class));
        verify(productReviewReplyRepository, never()).save(any(ProductReviewReply.class));
    }
}
