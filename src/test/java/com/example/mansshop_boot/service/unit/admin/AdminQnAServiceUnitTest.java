package com.example.mansshop_boot.service.unit.admin;

import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.Fixture.QnAClassificationFixture;
import com.example.mansshop_boot.domain.dto.admin.out.AdminQnAListResponseDTO;
import com.example.mansshop_boot.domain.dto.cache.CacheRequest;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyInsertDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.memberQnA.MemberQnARepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnAReplyRepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnARepository;
import com.example.mansshop_boot.repository.qnaClassification.QnAClassificationRepository;
import com.example.mansshop_boot.service.MyPageServiceImpl;
import com.example.mansshop_boot.service.PrincipalServiceImpl;
import com.example.mansshop_boot.service.admin.AdminCacheServiceImpl;
import com.example.mansshop_boot.service.admin.AdminQnAServiceImpl;
import com.example.mansshop_boot.Fixture.AdminPageDTOFixture;
import com.example.mansshop_boot.service.unit.fixture.MemberQnAUnitFixture;
import com.example.mansshop_boot.service.unit.fixture.ProductQnAUnitFixture;
import com.example.mansshop_boot.service.unit.fixture.QnADTOUnitFixture;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminQnAServiceUnitTest {

    @InjectMocks
    private AdminQnAServiceImpl adminQnAService;

    @Mock
    private AdminCacheServiceImpl adminCacheService;

    @Mock
    private PrincipalServiceImpl principalService;

    @Mock
    private MyPageServiceImpl myPageService;

    @Mock
    private ProductQnARepository productQnARepository;

    @Mock
    private ProductQnAReplyRepository productQnAReplyRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberQnARepository memberQnARepository;

    @Mock
    private QnAClassificationRepository qnAClassificationRepository;

    private AdminOrderPageDTO adminOrderPageDTOAllType;

    private AdminOrderPageDTO adminOrderPageDTONewType;

    private Principal principal;

    private List<ProductQnA> productQnAList;

    private ProductQnAReply productQnAReply;

    private List<MemberQnA> memberQnAList;

    private List<MemberQnAReply> memberQnAReplyList;

    private List<QnAClassification> qnaClassificationList;

    @BeforeAll
    void init() {
        List<Member> memberFixtureList = MemberAndAuthFixture.createDefaultMember(30).memberList();
        List<Product> productFixtureList = ProductFixture.createDefaultProductByOUTER(30);
        qnaClassificationList = QnAClassificationFixture.createUnitQnAClassificationList();
        productQnAList = ProductQnAUnitFixture.createProductQnAList(memberFixtureList, productFixtureList);
        memberQnAList = MemberQnAUnitFixture.createMemberQnAList(memberFixtureList, qnaClassificationList.get(0));
        productQnAReply = ProductQnAUnitFixture.createProductQnAReply(productQnAList.get(0));
        memberQnAReplyList = MemberQnAUnitFixture.createMemberQnAReplyList(memberQnAList.get(0));

        adminOrderPageDTOAllType = AdminPageDTOFixture.createAllListAdminOrderPageDTO();
        adminOrderPageDTONewType = AdminPageDTOFixture.createNewListAdminOrderPageDTO();
    }

    @BeforeEach
    void setUp() {
        principal = Mockito.mock(Principal.class);
    }

    @Test
    @DisplayName(value = "모든 상품 문의 목록 조회")
    void getProductQnAAllList() {
        List<AdminQnAListResponseDTO> response = QnADTOUnitFixture.getProductQnAResponseDTO(productQnAList);

        when(productQnARepository.findAllByAdminProductQnA(adminOrderPageDTOAllType))
                .thenReturn(response);
        when(adminCacheService.getFullScanCountCache(any(), any(CacheRequest.class)))
                .thenReturn((long) productQnAList.size());

        PagingListDTO<AdminQnAListResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminQnAService.getProductQnAList(adminOrderPageDTOAllType));

        verify(productQnARepository, never()).findAllByAdminProductQnACount(adminOrderPageDTOAllType);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertEquals(response.size(), result.content().size());
        Assertions.assertEquals(2, result.pagingData().getTotalPages());
        Assertions.assertEquals(productQnAList.size(), result.pagingData().getTotalElements());
    }

    @Test
    @DisplayName(value = "모든 상품 문의 목록 조회. 데이터가 없는 경우.")
    void getProductQnAAllListEmpty() {
        when(productQnARepository.findAllByAdminProductQnA(adminOrderPageDTOAllType))
                .thenReturn(Collections.emptyList());
        when(adminCacheService.getFullScanCountCache(any(), any(CacheRequest.class)))
                .thenReturn(0L);

        PagingListDTO<AdminQnAListResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminQnAService.getProductQnAList(adminOrderPageDTOAllType));

        verify(productQnARepository, never()).findAllByAdminProductQnACount(adminOrderPageDTOAllType);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.content().isEmpty());
        Assertions.assertEquals(0, result.pagingData().getTotalPages());
        Assertions.assertEquals(0, result.pagingData().getTotalElements());
    }

    @Test
    @DisplayName(value = "미처리 상품 문의 조회")
    void getProductQnANewList() {
        List<AdminQnAListResponseDTO> response = QnADTOUnitFixture.getProductQnAResponseDTO(productQnAList);

        when(productQnARepository.findAllByAdminProductQnA(adminOrderPageDTONewType))
                .thenReturn(response);
        when(productQnARepository.findAllByAdminProductQnACount(adminOrderPageDTONewType))
                .thenReturn((long) productQnAList.size());

        PagingListDTO<AdminQnAListResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminQnAService.getProductQnAList(adminOrderPageDTONewType));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertEquals(response.size(), result.content().size());
        Assertions.assertEquals(2, result.pagingData().getTotalPages());
        Assertions.assertEquals(productQnAList.size(), result.pagingData().getTotalElements());
    }

    @Test
    @DisplayName(value = "미처리 상품 문의 조회. 데이터가 없는 경우")
    void getProductQnANewListEmpty() {
        when(productQnARepository.findAllByAdminProductQnA(adminOrderPageDTONewType))
                .thenReturn(Collections.emptyList());
        when(productQnARepository.findAllByAdminProductQnACount(adminOrderPageDTONewType))
                .thenReturn(0L);

        PagingListDTO<AdminQnAListResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminQnAService.getProductQnAList(adminOrderPageDTONewType));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.content().isEmpty());
        Assertions.assertEquals(0, result.pagingData().getTotalPages());
        Assertions.assertEquals(0, result.pagingData().getTotalElements());
    }

    @Test
    @DisplayName(value = "상품 문의 답변 완료 처리. Entity가 없는 경우")
    void patchProductQnACompleteNotFound() {
        when(productQnARepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> adminQnAService.patchProductQnAComplete(1L)
        );

        verify(productQnARepository, never()).save(any(ProductQnA.class));
    }

    @Test
    @DisplayName(value = "상품 문의 답변 작성 처리. 상품 문의 조회가 실패한 경우")
    void postProductQnAReplyProductQnANotFound() {
        Member admin = Member.builder().userId("admin").build();
        QnAReplyInsertDTO insertDTO = QnADTOUnitFixture.getQnAReplyInsertDTO();

        when(memberRepository.findById(any())).thenReturn(Optional.of(admin));
        when(productQnARepository.findById(insertDTO.qnaId())).thenReturn(Optional.empty());

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> adminQnAService.postProductQnAReply(insertDTO, principal)
        );

        verify(productQnAReplyRepository, never()).save(any(ProductQnAReply.class));
        verify(productQnARepository, never()).save(any(ProductQnA.class));
    }

    @Test
    @DisplayName(value = "상품 문의 답변 수정 처리. 답변 조회가 실패한 경우")
    void patchProductQnAReplyEntityNotFound() {

        when(productQnAReplyRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> adminQnAService.patchProductQnAReply(QnADTOUnitFixture.getQnAReplyDTO(), principal)
        );

        verify(principalService, never()).getUserIdByPrincipal(principal);
        verify(productQnAReplyRepository, never()).save(any(ProductQnAReply.class));
    }

    @Test
    @DisplayName(value = "상품 문의 답변 수정 처리. 작성자(관리자)가 불일치하는 경우")
    void patchProductQnAReplyWriterNotEquals() {
        QnAReplyDTO replyDTO = QnADTOUnitFixture.getQnAReplyDTO();
        when(productQnAReplyRepository.findById(replyDTO.replyId())).thenReturn(Optional.of(productQnAReply));
        when(principalService.getUserIdByPrincipal(principal)).thenReturn("admin1");

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> adminQnAService.patchProductQnAReply(replyDTO, principal)
        );

        verify(productQnAReplyRepository, never()).save(any(ProductQnAReply.class));
    }

    @Test
    @DisplayName(value = "회원 문의 전체 목록 조회")
    void getMemberQnAAllList() {
        List<AdminQnAListResponseDTO> response = QnADTOUnitFixture.getMemberQnAResponseDTO(memberQnAList);

        when(memberQnARepository.findAllByAdminMemberQnA(adminOrderPageDTOAllType))
                .thenReturn(response);
        when(adminCacheService.getFullScanCountCache(any(), any(CacheRequest.class)))
                .thenReturn((long) memberQnAList.size());

        PagingListDTO<AdminQnAListResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminQnAService.getMemberQnAList(adminOrderPageDTOAllType));

        verify(memberQnARepository, never()).findAllByAdminMemberQnACount(adminOrderPageDTOAllType);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertEquals(response.size(), result.content().size());
        Assertions.assertEquals(2, result.pagingData().getTotalPages());
        Assertions.assertEquals(memberQnAList.size(), result.pagingData().getTotalElements());
    }

    @Test
    @DisplayName(value = "회원 문의 전체 목록 조회. 데이터가 없는 경우")
    void getMemberQnAAllListEmpty() {

        when(memberQnARepository.findAllByAdminMemberQnA(adminOrderPageDTOAllType))
                .thenReturn(Collections.emptyList());
        when(adminCacheService.getFullScanCountCache(any(), any(CacheRequest.class)))
                .thenReturn(0L);

        PagingListDTO<AdminQnAListResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminQnAService.getMemberQnAList(adminOrderPageDTOAllType));

        verify(memberQnARepository, never()).findAllByAdminMemberQnACount(adminOrderPageDTOAllType);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.content().isEmpty());
        Assertions.assertEquals(0, result.pagingData().getTotalPages());
        Assertions.assertEquals(0, result.pagingData().getTotalElements());
    }

    @Test
    @DisplayName(value = "회원 문의 미처리 목록 조회")
    void getMemberQnANewList() {
        List<AdminQnAListResponseDTO> response = QnADTOUnitFixture.getMemberQnAResponseDTO(memberQnAList);

        when(memberQnARepository.findAllByAdminMemberQnA(adminOrderPageDTONewType))
                .thenReturn(response);
        when(memberQnARepository.findAllByAdminMemberQnACount(adminOrderPageDTONewType))
                .thenReturn((long) memberQnAList.size());

        PagingListDTO<AdminQnAListResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminQnAService.getMemberQnAList(adminOrderPageDTONewType));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertEquals(response.size(), result.content().size());
        Assertions.assertEquals(2, result.pagingData().getTotalPages());
        Assertions.assertEquals(memberQnAList.size(), result.pagingData().getTotalElements());
    }

    @Test
    @DisplayName(value = "회원 문의 미처리 목록 조회. 데이터가 없는 경우")
    void getMemberQnANewListEmpty() {

        when(memberQnARepository.findAllByAdminMemberQnA(adminOrderPageDTONewType))
                .thenReturn(Collections.emptyList());
        when(memberQnARepository.findAllByAdminMemberQnACount(adminOrderPageDTONewType))
                .thenReturn(0L);

        PagingListDTO<AdminQnAListResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminQnAService.getMemberQnAList(adminOrderPageDTONewType));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.content().isEmpty());
        Assertions.assertEquals(0, result.pagingData().getTotalPages());
        Assertions.assertEquals(0, result.pagingData().getTotalElements());
    }

    @Test
    @DisplayName(value = "회원 문의 답변 완료 처리. 데이터가 없는 경우")
    void patchMemberQnACompleteNotFound() {

        when(memberQnARepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> adminQnAService.patchMemberQnAComplete(1L)
        );

        verify(memberQnARepository, never()).save(any(MemberQnA.class));
    }

    @Test
    @DisplayName(value = "회원 문의 답변 작성 실패")
    void postMemberQnAReplyFail() {
        QnAReplyInsertDTO insertDTO = QnADTOUnitFixture.getQnAReplyInsertDTO();
        when(myPageService.postMemberQnAReply(insertDTO, principal))
                .thenReturn(Result.FAIL.getResultKey());

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> adminQnAService.postMemberQnAReply(insertDTO, principal)
        );
    }
}
