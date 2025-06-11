package com.example.mansshop_boot.service.integration.admin;

import com.example.mansshop_boot.Fixture.*;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.admin.out.AdminQnAClassificationDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminQnAListResponseDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.business.MyPageQnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyInsertDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.RedisCaching;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.memberQnA.MemberQnAReplyRepository;
import com.example.mansshop_boot.repository.memberQnA.MemberQnARepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnAReplyRepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnARepository;
import com.example.mansshop_boot.repository.qnaClassification.QnAClassificationRepository;
import com.example.mansshop_boot.service.admin.AdminQnAService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
@ActiveProfiles("test")
@Transactional
public class AdminQnAServiceIT {

    @Autowired
    private AdminQnAService adminQnAService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ClassificationRepository classificationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private ProductQnARepository productQnARepository;

    @Autowired
    private ProductQnAReplyRepository productQnAReplyRepository;

    @Autowired
    private QnAClassificationRepository qnAClassificationRepository;

    @Autowired
    private MemberQnARepository memberQnARepository;

    @Autowired
    private MemberQnAReplyRepository memberQnAReplyRepository;

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    private List<Product> productList;

    private List<Member> memberList;

    private Member admin;

    private List<ProductQnA> newProductQnAList;

    private List<ProductQnA> allProductQnA;

    private List<ProductQnAReply> productQnAReplyList;

    private List<QnAClassification> qnAClassificationList;

    private List<MemberQnA> newMemberQnAList;

    private List<MemberQnA> allMemberQnA;

    private Principal principal;

    private static String ALL_LIST_TYPE = "all";

    private static String NEW_LIST_TYPE = "new";

    private static String PRODUCT_QNA_CACHING_KEY = RedisCaching.ADMIN_PRODUCT_QNA_COUNT.getKey();

    private static String MEMBER_QNA_CACHING_KEY = RedisCaching.ADMIN_MEMBER_QNA_COUNT.getKey();

    @BeforeEach
    void init() {
        MemberAndAuthFixtureDTO memberAndAuthFixture = MemberAndAuthFixture.createDefaultMember(30);
        memberList = memberAndAuthFixture.memberList();
        memberRepository.saveAll(memberList);
        authRepository.saveAll(memberAndAuthFixture.authList());
        List<Classification> classificationFixture = ClassificationFixture.createClassification();
        classificationRepository.saveAll(classificationFixture);

        productList = ProductFixture.createSaveProductList(10, classificationFixture.get(0));
        productRepository.saveAll(productList);

        qnAClassificationList = QnAClassificationFixture.createQnAClassificationList();
        qnAClassificationRepository.saveAll(qnAClassificationList);

        MemberAndAuthFixtureDTO adminFixture = MemberAndAuthFixture.createAdmin();
        admin = adminFixture.memberList().get(0);
        memberRepository.save(admin);
        authRepository.save(adminFixture.authList().get(0));

        newProductQnAList = ProductQnAFixture.createDefaultProductQnA(memberList, productList);
        List<Member> completeQnAMemberFixture = memberList.stream().limit(5).toList();
        List<Product> completeQnAProductFixture = productList.stream().limit(5).toList();
        List<ProductQnA> completeProductQnA = ProductQnAFixture.createProductQnACompletedAnswer(completeQnAMemberFixture, completeQnAProductFixture);
        allProductQnA = new ArrayList<>(newProductQnAList);
        allProductQnA.addAll(completeProductQnA);
        productQnARepository.saveAll(allProductQnA);

        productQnAReplyList = ProductQnAFixture.createDefaultProductQnaReply(admin, completeProductQnA);
        productQnAReplyRepository.saveAll(productQnAReplyList);

        newMemberQnAList = MemberQnAFixture.createDefaultMemberQnA(qnAClassificationList, memberList);
        List<MemberQnA> completeMemberQnA = MemberQnAFixture.createMemberQnACompletedAnswer(qnAClassificationList, completeQnAMemberFixture);
        allMemberQnA = new ArrayList<>(newMemberQnAList);
        allMemberQnA.addAll(completeMemberQnA);
        memberQnARepository.saveAll(allMemberQnA);

        List<MemberQnAReply> memberQnAReplyList = MemberQnAFixture.createMemberQnAReply(completeMemberQnA, admin);
        memberQnAReplyRepository.saveAll(memberQnAReplyList);

        principal = () -> admin.getUserId();
    }

    private int getTotalPages(int listSize, int amount) {
        return (int) Math.ceil((double) listSize / amount);
    }

    @Test
    @DisplayName(value = "모든 상품 문의 목록 조회.")
    void getAllProductQnAList() {
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminOrderPageDTO(null, ALL_LIST_TYPE, 1);
        int totalPages = getTotalPages(allProductQnA.size(), pageDTO.amount());
        PagingListDTO<AdminQnAListResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminQnAService.getProductQnAList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertFalse(result.pagingData().isEmpty());
        Assertions.assertEquals(pageDTO.amount(), result.content().size());
        Assertions.assertEquals(allProductQnA.size(), result.pagingData().getTotalElements());
        Assertions.assertEquals(totalPages, result.pagingData().getTotalPages());

        Long cachingResult = redisTemplate.opsForValue().get(PRODUCT_QNA_CACHING_KEY);

        Assertions.assertNotNull(cachingResult);
        Assertions.assertEquals(allProductQnA.size(), cachingResult);

        redisTemplate.delete(PRODUCT_QNA_CACHING_KEY);
    }

    @Test
    @DisplayName(value = "모든 상품 문의 목록 조회. 데이터가 없는 경우.")
    void getAllProductQnAListEmpty() {
        productQnAReplyRepository.deleteAll();
        productQnARepository.deleteAll();
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminOrderPageDTO(null, ALL_LIST_TYPE, 1);

        PagingListDTO<AdminQnAListResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminQnAService.getProductQnAList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.content().isEmpty());
        Assertions.assertTrue(result.pagingData().isEmpty());
        Assertions.assertEquals(0, result.pagingData().getTotalElements());
        Assertions.assertEquals(0, result.pagingData().getTotalPages());

        Long cachingResult = redisTemplate.opsForValue().get(PRODUCT_QNA_CACHING_KEY);

        Assertions.assertNull(cachingResult);
    }

    @Test
    @DisplayName(value = "모든 상품 문의 목록 조회. 검색")
    void getAllProductQnAListSearch() {
        Member searchMemberFixture = memberList.get(0);
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminOrderPageDTO(searchMemberFixture.getUserId(), ALL_LIST_TYPE, 1);
        int totalElements = allProductQnA.stream()
                                        .filter(v ->
                                                v.getMember().getUserId().equals(searchMemberFixture.getUserId()))
                                        .toList()
                                        .size();
        int totalPages = getTotalPages(totalElements, pageDTO.amount());
        int resultContentSize = Math.min(totalElements, pageDTO.amount());
        PagingListDTO<AdminQnAListResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminQnAService.getProductQnAList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertFalse(result.pagingData().isEmpty());
        Assertions.assertEquals(resultContentSize, result.content().size());
        Assertions.assertEquals(totalElements, result.pagingData().getTotalElements());
        Assertions.assertEquals(totalPages, result.pagingData().getTotalPages());

        Long cachingResult = redisTemplate.opsForValue().get(PRODUCT_QNA_CACHING_KEY);

        Assertions.assertNull(cachingResult);
    }

    @Test
    @DisplayName(value = "모든 상품 문의 목록 조회. 검색. 결과가 없는 경우")
    void getAllProductQnAListSearchEmpty() {
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminOrderPageDTO("NoneUser", ALL_LIST_TYPE, 1);
        PagingListDTO<AdminQnAListResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminQnAService.getProductQnAList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.content().isEmpty());
        Assertions.assertTrue(result.pagingData().isEmpty());
        Assertions.assertEquals(0, result.pagingData().getTotalElements());
        Assertions.assertEquals(0, result.pagingData().getTotalPages());

        Long cachingResult = redisTemplate.opsForValue().get(PRODUCT_QNA_CACHING_KEY);

        Assertions.assertNull(cachingResult);
    }

    @Test
    @DisplayName(value = "새로운 상품 문의 목록 조회.")
    void getNewProductQnAList() {
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminOrderPageDTO(null, NEW_LIST_TYPE, 1);
        int totalPages = getTotalPages(newProductQnAList.size(), pageDTO.amount());
        PagingListDTO<AdminQnAListResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminQnAService.getProductQnAList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertFalse(result.pagingData().isEmpty());
        Assertions.assertEquals(pageDTO.amount(), result.content().size());
        Assertions.assertEquals(newProductQnAList.size(), result.pagingData().getTotalElements());
        Assertions.assertEquals(totalPages, result.pagingData().getTotalPages());

        Long cachingResult = redisTemplate.opsForValue().get(PRODUCT_QNA_CACHING_KEY);

        Assertions.assertNull(cachingResult);
    }

    @Test
    @DisplayName(value = "미처리 상품 문의 목록 조회. 검색")
    void getNewProductQnAListSearch() {
        Member searchMemberFixture = memberList.get(0);
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminOrderPageDTO(searchMemberFixture.getUserId(), NEW_LIST_TYPE, 1);
        int totalElements = newProductQnAList.stream()
                .filter(v ->
                        v.getMember().getUserId().equals(searchMemberFixture.getUserId()))
                .toList()
                .size();
        int totalPages = getTotalPages(totalElements, pageDTO.amount());
        int resultContentSize = Math.min(totalElements, pageDTO.amount());
        PagingListDTO<AdminQnAListResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminQnAService.getProductQnAList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertFalse(result.pagingData().isEmpty());
        Assertions.assertEquals(resultContentSize, result.content().size());
        Assertions.assertEquals(totalElements, result.pagingData().getTotalElements());
        Assertions.assertEquals(totalPages, result.pagingData().getTotalPages());

        Long cachingResult = redisTemplate.opsForValue().get(PRODUCT_QNA_CACHING_KEY);

        Assertions.assertNull(cachingResult);
    }

    @Test
    @DisplayName(value = "상품 문의 답변 완료 처리")
    void patchProductQnAComplete() {
        ProductQnA newProductQnAFixture = newProductQnAList.get(0);

        String result = Assertions.assertDoesNotThrow(() -> adminQnAService.patchProductQnAComplete(newProductQnAFixture.getId()));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(Result.OK.getResultKey(), result);

        ProductQnA patchFixture = productQnARepository.findById(newProductQnAFixture.getId()).orElse(null);

        Assertions.assertNotNull(patchFixture);
        Assertions.assertTrue(patchFixture.isProductQnAStat());
    }

    @Test
    @DisplayName(value = "상품 문의 답변 완료 처리. 데이터가 없는 경우")
    void patchProductQnACompleteNotFound() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> adminQnAService.patchProductQnAComplete(0L)
        );
    }

    @Test
    @DisplayName(value = "상품 문의 답변 작성 처리")
    void postProductQnAReply() {
        ProductQnA newProductQnAFixture = newProductQnAList.get(0);
        String content = "test Reply Content";
        QnAReplyInsertDTO insertDTO = new QnAReplyInsertDTO(newProductQnAFixture.getId(), content);

        String result = Assertions.assertDoesNotThrow(() -> adminQnAService.postProductQnAReply(insertDTO, principal));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(Result.OK.getResultKey(), result);

        List<MyPageQnAReplyDTO> saveReplyList = productQnAReplyRepository.findAllByQnAId(newProductQnAFixture.getId());

        Assertions.assertFalse(saveReplyList.isEmpty());

        MyPageQnAReplyDTO saveReply = saveReplyList.get(0);

        Assertions.assertEquals(admin.getNickname(), saveReply.writer());
        Assertions.assertEquals(content, saveReply.replyContent());

        ProductQnA patchProductQnA = productQnARepository.findById(newProductQnAFixture.getId()).orElse(null);

        Assertions.assertNotNull(patchProductQnA);
        Assertions.assertTrue(patchProductQnA.isProductQnAStat());
    }

    @Test
    @DisplayName(value = "상품 문의 답변 작성 처리. 상품 문의 데이터가 존재하지 않는 경우")
    void postProductQnAReplyNotFound() {
        String content = "test Reply Content";
        QnAReplyInsertDTO insertDTO = new QnAReplyInsertDTO(0L, content);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> adminQnAService.postProductQnAReply(insertDTO, principal)
        );
    }

    @Test
    @DisplayName(value = "상품 문의 답변 수정")
    void patchProductQnAReply() {
        ProductQnAReply reply = productQnAReplyList.get(0);
        String content = "patch reply content";
        QnAReplyDTO replyDTO = new QnAReplyDTO(reply.getId(), content);

        String result = Assertions.assertDoesNotThrow(() -> adminQnAService.patchProductQnAReply(replyDTO, principal));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(Result.OK.getResultKey(), result);

        ProductQnAReply patchReply = productQnAReplyRepository.findById(reply.getId()).orElse(null);

        Assertions.assertNotNull(patchReply);
        Assertions.assertEquals(content, patchReply.getReplyContent());
    }

    @Test
    @DisplayName(value = "상품 문의 답변 수정. 답변 데이터가 없는 경우")
    void patchProductQnAReplyNotFound() {
        String content = "patch reply content";
        QnAReplyDTO replyDTO = new QnAReplyDTO(0L, content);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> adminQnAService.patchProductQnAReply(replyDTO, principal)
        );
    }

    @Test
    @DisplayName(value = "상품 문의 답변 수정. 작성자가 일치하지 않는 경우")
    void patchProductQnAReplyNotEqualsWriter() {
        ProductQnAReply reply = productQnAReplyList.get(0);
        String content = "patch reply content";
        QnAReplyDTO replyDTO = new QnAReplyDTO(reply.getId(), content);

        Principal nonAdminPrincipal = () -> "writer";

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> adminQnAService.patchProductQnAReply(replyDTO, nonAdminPrincipal)
        );
    }

    @Test
    @DisplayName(value = "전체 회원 문의 목록 조회")
    void getAllMemberQnAList() {
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminOrderPageDTO(null, ALL_LIST_TYPE, 1);
        int totalPages = getTotalPages(allMemberQnA.size(), pageDTO.amount());
        PagingListDTO<AdminQnAListResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminQnAService.getMemberQnAList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertFalse(result.pagingData().isEmpty());
        Assertions.assertEquals(pageDTO.amount(), result.content().size());
        Assertions.assertEquals(allMemberQnA.size(), result.pagingData().getTotalElements());
        Assertions.assertEquals(totalPages, result.pagingData().getTotalPages());

        Long cachingResult = redisTemplate.opsForValue().get(MEMBER_QNA_CACHING_KEY);

        Assertions.assertNotNull(cachingResult);
        Assertions.assertEquals(allMemberQnA.size(), cachingResult);

        redisTemplate.delete(MEMBER_QNA_CACHING_KEY);
    }

    @Test
    @DisplayName(value = "전체 회원 문의 목록 조회. 데이터가 없는 경우")
    void getAllMemberQnAListEmpty() {
        memberQnAReplyRepository.deleteAll();
        memberQnARepository.deleteAll();
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminOrderPageDTO(null, ALL_LIST_TYPE, 1);

        PagingListDTO<AdminQnAListResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminQnAService.getMemberQnAList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.content().isEmpty());
        Assertions.assertTrue(result.pagingData().isEmpty());
        Assertions.assertEquals(0, result.pagingData().getTotalElements());
        Assertions.assertEquals(0, result.pagingData().getTotalPages());

        Long cachingResult = redisTemplate.opsForValue().get(MEMBER_QNA_CACHING_KEY);

        Assertions.assertNull(cachingResult);
    }

    @Test
    @DisplayName(value = "모든 회원 문의 목록 조회. 검색")
    void getAllMemberQnAListSearch() {
        allMemberQnA.forEach(v -> System.out.println("testMethod memberQNA : " + v));
        Member searchMemberFixture = memberList.get(0);
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminOrderPageDTO(searchMemberFixture.getUserId(), ALL_LIST_TYPE, 1);
        int totalElements = allMemberQnA.stream()
                .filter(v ->
                        v.getMember().getUserId().equals(searchMemberFixture.getUserId()))
                .toList()
                .size();
        int totalPages = getTotalPages(totalElements, pageDTO.amount());
        int resultContentSize = Math.min(totalElements, pageDTO.amount());
        PagingListDTO<AdminQnAListResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminQnAService.getMemberQnAList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertFalse(result.pagingData().isEmpty());
        Assertions.assertEquals(resultContentSize, result.content().size());
        Assertions.assertEquals(totalElements, result.pagingData().getTotalElements());
        Assertions.assertEquals(totalPages, result.pagingData().getTotalPages());

        Long cachingResult = redisTemplate.opsForValue().get(MEMBER_QNA_CACHING_KEY);

        Assertions.assertNull(cachingResult);
    }

    @Test
    @DisplayName(value = "모든 상품 문의 목록 조회. 검색. 결과가 없는 경우")
    void getAllMemberQnAListSearchEmpty() {
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminOrderPageDTO("NoneUser", ALL_LIST_TYPE, 1);
        PagingListDTO<AdminQnAListResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminQnAService.getMemberQnAList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.content().isEmpty());
        Assertions.assertTrue(result.pagingData().isEmpty());
        Assertions.assertEquals(0, result.pagingData().getTotalElements());
        Assertions.assertEquals(0, result.pagingData().getTotalPages());

        Long cachingResult = redisTemplate.opsForValue().get(MEMBER_QNA_CACHING_KEY);

        Assertions.assertNull(cachingResult);
    }

    @Test
    @DisplayName(value = "회원 문의 답변 완료 처리")
    void patchMemberQnAComplete() {
        MemberQnA memberQnA = newMemberQnAList.get(0);

        String result = Assertions.assertDoesNotThrow(() -> adminQnAService.patchMemberQnAComplete(memberQnA.getId()));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(Result.OK.getResultKey(), result);

        MemberQnA patchMemberQnA = memberQnARepository.findById(memberQnA.getId()).orElse(null);

        Assertions.assertNotNull(patchMemberQnA);
        Assertions.assertTrue(patchMemberQnA.isMemberQnAStat());
    }

    @Test
    @DisplayName(value = "회원 문의 답변 완료 처리. 회원 문의 데이터가 없는 경우")
    void patchMemberQnACompleteNotFound() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> adminQnAService.patchMemberQnAComplete(0L)
        );
    }

    @Test
    @DisplayName(value = "회원 문의 답변 작성")
    void postMemberQnAReply() {
        MemberQnA memberQnA = newMemberQnAList.get(0);
        String content = "test Reply Content";
        QnAReplyInsertDTO insertDTO = new QnAReplyInsertDTO(memberQnA.getId(), content);

        String result = Assertions.assertDoesNotThrow(() -> adminQnAService.postMemberQnAReply(insertDTO, principal));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(Result.OK.getResultKey(), result);

        List<MyPageQnAReplyDTO> replyList = memberQnAReplyRepository.findAllByQnAId(memberQnA.getId());
        Assertions.assertFalse(replyList.isEmpty());

        MyPageQnAReplyDTO reply = replyList.get(0);

        Assertions.assertEquals(admin.getNickname(), reply.writer());
        Assertions.assertEquals(content, reply.replyContent());

        MemberQnA patchMemberQnA = memberQnARepository.findById(memberQnA.getId()).orElse(null);
        Assertions.assertNotNull(patchMemberQnA);
        Assertions.assertTrue(patchMemberQnA.isMemberQnAStat());
    }

    @Test
    @DisplayName(value = "회원 문의 답변 작성. 회원 문의 데이터가 없는 경우")
    void postMemberQnAReplyNotFound() {
        String content = "test Reply Content";
        QnAReplyInsertDTO insertDTO = new QnAReplyInsertDTO(0L, content);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> adminQnAService.postMemberQnAReply(insertDTO, principal)
        );
    }

    @Test
    @DisplayName(value = "모든 회원 문의 분류 조회")
    void getQnAClassification() {
        List<AdminQnAClassificationDTO> result = Assertions.assertDoesNotThrow(() -> adminQnAService.getQnAClassification());

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(qnAClassificationList.size(), result.size());

        for(int i = 0; i < qnAClassificationList.size(); i++) {
            QnAClassification entity = qnAClassificationList.get(i);
            AdminQnAClassificationDTO resultDTO = result.get(i);

            Assertions.assertEquals(entity.getId(), resultDTO.id());
            Assertions.assertEquals(entity.getQnaClassificationName(), resultDTO.name());
        }
    }

    @Test
    @DisplayName(value = "모든 회원 문의 분류 조회. 데이터가 없는 경우.")
    void getQnAClassificationEmpty() {
        memberQnAReplyRepository.deleteAll();
        memberQnARepository.deleteAll();
        qnAClassificationRepository.deleteAll();
        List<AdminQnAClassificationDTO> result = Assertions.assertDoesNotThrow(() -> adminQnAService.getQnAClassification());

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName(value = "회원 문의 분류 추가")
    void postQnAClassification() {
        String classificationName = "테스트 분류";

        String result = Assertions.assertDoesNotThrow(() -> adminQnAService.postQnAClassification(classificationName));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(Result.OK.getResultKey(), result);

        List<QnAClassification> allQnAClassificationList = qnAClassificationRepository.findAll();

        Assertions.assertFalse(allQnAClassificationList.isEmpty());
        Assertions.assertEquals(qnAClassificationList.size() + 1, allQnAClassificationList.size());
        Assertions.assertEquals(classificationName, allQnAClassificationList.get(allQnAClassificationList.size() - 1).getQnaClassificationName());
    }
}