package com.example.mansshop_boot.service.integration;

import com.example.mansshop_boot.Fixture.*;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.Fixture.util.PaginationUtils;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.config.customException.exception.CustomNotFoundException;
import com.example.mansshop_boot.domain.dto.mypage.business.MemberOrderDTO;
import com.example.mansshop_boot.domain.dto.mypage.business.MyPagePageDTO;
import com.example.mansshop_boot.domain.dto.mypage.in.MyPageInfoPatchDTO;
import com.example.mansshop_boot.domain.dto.mypage.in.MyPagePatchReviewDTO;
import com.example.mansshop_boot.domain.dto.mypage.in.MyPagePostReviewDTO;
import com.example.mansshop_boot.domain.dto.mypage.out.*;
import com.example.mansshop_boot.domain.dto.mypage.qna.business.MyPageQnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.MemberQnAInsertDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.MemberQnAModifyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyInsertDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.out.*;
import com.example.mansshop_boot.domain.dto.pageable.LikePageDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.MailSuffix;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
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
import com.example.mansshop_boot.repository.productReview.ProductReviewReplyRepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewRepository;
import com.example.mansshop_boot.repository.qnaClassification.QnAClassificationRepository;
import com.example.mansshop_boot.service.MyPageService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
@ActiveProfiles("test")
@Transactional
public class MyPageServiceIT {

    @Autowired
    private MyPageService myPageService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClassificationRepository classificationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private ProductLikeRepository productLikeRepository;

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
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private ProductOrderDetailRepository productOrderDetailRepository;

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Autowired
    private ProductReviewReplyRepository productReviewReplyRepository;

    private List<Member> memberList;

    private Member member;

    private Principal principal;

    private List<Product> productList;

    private List<ProductOption> productOptionList;

    private List<ProductLike> productLikeList;

    private List<ProductQnA> allProductQnAList;

    private List<ProductQnA> newProductQnAList;

    private List<ProductQnAReply> productQnAReplyList;

    private List<QnAClassification> qnAClassificationList;

    private List<MemberQnA> allMemberQnAList;

    private List<MemberQnA> answerMemberQnAList;

    private List<MemberQnAReply> memberQnAReplyList;

    private List<ProductReview> allProductReviewList;

    private List<ProductReviewReply> productReviewReplyList;

    private List<ProductOrder> productOrderList;

    @BeforeEach
    void init() {
        /*
            member
            auth
            product
            option
            productLike
            productQnA
            productQnAReply
            QnAClassification
            memberQnA
            memberQnAReply
            productOrder
         */

        MemberAndAuthFixtureDTO memberAndAuthFixture = MemberAndAuthFixture.createDefaultMember(2);
        MemberAndAuthFixtureDTO adminFixture = MemberAndAuthFixture.createAdmin();
        Member admin = adminFixture.memberList().get(0);
        memberList = memberAndAuthFixture.memberList();
        member = memberList.get(0);
        List<Member> saveMemberList = new ArrayList<>(memberList);
        saveMemberList.addAll(adminFixture.memberList());
        memberRepository.saveAll(saveMemberList);
        principal = () -> member.getUserId();


        List<Classification> classificationList = ClassificationFixture.createClassification();
        classificationRepository.saveAll(classificationList);

        productList = ProductFixture.createSaveProductList(50, classificationList.get(0));
        productOptionList = productList.stream().flatMap(v -> v.getProductOptions().stream()).toList();
        productRepository.saveAll(productList);
        productOptionRepository.saveAll(productOptionList);

        List<Product> likeProducts = productList.stream().limit(30).toList();
        productLikeList = ProductLikeFixture.createDefaultProductLike(memberList, likeProducts);

        productLikeRepository.saveAll(productLikeList);

        List<ProductQnA> productQnAList = ProductQnAFixture.createProductQnACompletedAnswer(memberList, likeProducts);
        productQnAReplyList = ProductQnAFixture.createDefaultProductQnaReply(admin, productQnAList);
        newProductQnAList = ProductQnAFixture.createDefaultProductQnA(memberList, likeProducts);
        allProductQnAList = new ArrayList<>(productQnAList);
        allProductQnAList.addAll(newProductQnAList);

        productQnARepository.saveAll(allProductQnAList);
        productQnAReplyRepository.saveAll(productQnAReplyList);

        qnAClassificationList = QnAClassificationFixture.createQnAClassificationList();
        qnAClassificationRepository.saveAll(qnAClassificationList);

        answerMemberQnAList = MemberQnAFixture.createMemberQnACompletedAnswer(qnAClassificationList, memberList);
        memberQnAReplyList = MemberQnAFixture.createMemberQnAReply(answerMemberQnAList, admin);
        List<MemberQnA> newMemberQnAList = MemberQnAFixture.createDefaultMemberQnA(qnAClassificationList, memberList);
        allMemberQnAList = new ArrayList<>(answerMemberQnAList);
        allMemberQnAList.addAll(newMemberQnAList);

        memberQnARepository.saveAll(allMemberQnAList);
        memberQnAReplyRepository.saveAll(memberQnAReplyList);

        List<ProductReview> productReviewList = ProductReviewFixture.createReviewWithCompletedAnswer(memberList, productOptionList);
        productReviewReplyList = ProductReviewFixture.createDefaultReviewReply(productReviewList, admin);
        List<ProductReview> newProductReviewList = ProductReviewFixture.createDefaultReview(memberList, productOptionList);
        allProductReviewList = new ArrayList<>(productReviewList);
        allProductReviewList.addAll(newProductReviewList);

        productReviewRepository.saveAll(allProductReviewList);
        productReviewReplyRepository.saveAll(productReviewReplyList);

        productOrderList = ProductOrderFixture.createDefaultProductOrder(memberList, productOptionList);
        productOrderRepository.saveAll(productOrderList);
    }

    private List<ProductOrder> getMemberProductOrderList(Member member, int limit) {
        int size = getLimitSize(productOrderList.size(), limit);

        return productOrderList.stream()
                .filter(v -> userIdEquals(v.getMember(), member))
                .limit(size)
                .toList();
    }

    private List<ProductLike> getMemberProductLikeList(Member member, int limit) {
        int size = getLimitSize(productLikeList.size(), limit);

        return productLikeList.stream()
                .filter(v -> userIdEquals(v.getMember(), member))
                .limit(size)
                .toList();
    }

    private List<ProductQnA> getMemberProductQnAList(Member member, int limit) {
        int size = getLimitSize(allProductQnAList.size(), limit);

        return allProductQnAList.stream()
                .filter(v -> userIdEquals(v.getMember(), member))
                .limit(size)
                .toList();
    }

    private List<MemberQnA> getMemberMemberQnAList(Member member, int limit) {
        int size = getLimitSize(allMemberQnAList.size(), limit);

        return allMemberQnAList.stream()
                .filter(v -> userIdEquals(v.getMember(), member))
                .limit(size)
                .toList();
    }

    private List<ProductReview> getMemberProductReviewList(Member member, int limit) {
        int size = getLimitSize(allProductReviewList.size(), limit);

        return allProductReviewList.stream()
                .filter(v -> userIdEquals(v.getMember(), member))
                .limit(size)
                .toList();
    }

    private int getLimitSize(int listSize, int limit) {
        return limit == 0 ? listSize : limit;
    }

    private boolean userIdEquals(Member listMember, Member member) {
        return listMember.getUserId().equals(member.getUserId());
    }

    @Test
    @DisplayName(value = "주문 목록 조회")
    void getOrderList() {
        OrderPageDTO pageDTO = PageDTOFixture.createDefaultOrderPageDTO("3");
        MemberOrderDTO memberOrderDTO = new MemberOrderDTO(member.getUserId(), null, null);
        List<ProductOrder> orderFixture = getMemberProductOrderList(member, 0);
        int totalPages = PaginationUtils.getTotalPages(orderFixture.size(), pageDTO.orderAmount());
        PagingListDTO<MyPageOrderDTO> result = assertDoesNotThrow(() -> myPageService.getOrderList(pageDTO, memberOrderDTO));

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertFalse(result.pagingData().isEmpty());
        assertEquals(orderFixture.size(), result.pagingData().getTotalElements());
        assertEquals(totalPages, result.pagingData().getTotalPages());
    }

    @Test
    @DisplayName(value = "주문 목록 조회. 데이터가 없는 경우")
    void getOrderListEmpty() {
        OrderPageDTO pageDTO = PageDTOFixture.createDefaultOrderPageDTO("3");
        MemberOrderDTO memberOrderDTO = new MemberOrderDTO(member.getUserId(), null, null);
        productOrderRepository.deleteAll();

        PagingListDTO<MyPageOrderDTO> result = assertDoesNotThrow(() -> myPageService.getOrderList(pageDTO, memberOrderDTO));

        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertTrue(result.pagingData().isEmpty());
        assertEquals(0, result.pagingData().getTotalElements());
        assertEquals(0, result.pagingData().getTotalPages());
    }

    @Test
    @DisplayName(value = "관심 상품 목록 조회")
    void getLikeList() {
        LikePageDTO pageDTO = PageDTOFixture.createDefaultLikePageDTO(1);
        List<ProductLike> fixtureList = getMemberProductLikeList(member, 0);
        int totalPages = PaginationUtils.getTotalPages(fixtureList.size(), pageDTO.likeAmount());
        int pageElements = Math.min(fixtureList.size(), pageDTO.likeAmount());
        Page<ProductLikeDTO> result = assertDoesNotThrow(() -> myPageService.getLikeList(pageDTO, principal));

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertFalse(result.isEmpty());
        assertEquals(fixtureList.size(), result.getTotalElements());
        assertEquals(totalPages, result.getTotalPages());
        assertEquals(pageElements, result.getContent().size());
    }

    @Test
    @DisplayName(value = "관심 상품 목록 조회. 데이터가 없는 경우")
    void getLikeListEmpty() {
        productLikeRepository.deleteAll();
        LikePageDTO pageDTO = PageDTOFixture.createDefaultLikePageDTO(1);

        Page<ProductLikeDTO> result = assertDoesNotThrow(() -> myPageService.getLikeList(pageDTO, principal));

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    @DisplayName(value = "상품 문의 목록 조회")
    void getProductQnAList() {
        MyPagePageDTO pageDTO = PageDTOFixture.createDefaultMyPagePageDTO(1);
        List<ProductQnA> fixtureList = getMemberProductQnAList(member, 0);
        int totalPages = PaginationUtils.getTotalPages(fixtureList.size(), pageDTO.amount());
        int pageElements = Math.min(fixtureList.size(), pageDTO.amount());

        Page<ProductQnAListDTO> result = assertDoesNotThrow(() -> myPageService.getProductQnAList(pageDTO, principal));

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertFalse(result.isEmpty());
        assertEquals(fixtureList.size(), result.getTotalElements());
        assertEquals(totalPages, result.getTotalPages());
        assertEquals(pageElements, result.getContent().size());
    }

    @Test
    @DisplayName(value = "상품 문의 목록 조회. 데이터가 없는 경우")
    void getProductQnAListEmpty() {
        productQnARepository.deleteAll();
        MyPagePageDTO pageDTO = PageDTOFixture.createDefaultMyPagePageDTO(1);

        Page<ProductQnAListDTO> result = assertDoesNotThrow(() -> myPageService.getProductQnAList(pageDTO, principal));

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    @DisplayName(value = "상품 문의 상세 조회. 답변이 있는 경우")
    void getProductQnADetail() {
        ProductQnAReply fixture = productQnAReplyList.get(0);
        ProductQnA fixtureQnA = fixture.getProductQnA();
        Principal fixturePrincipal = () -> fixtureQnA.getMember().getUserId();
        long qnaId = fixture.getProductQnA().getId();

        ProductQnADetailDTO result = assertDoesNotThrow(() -> myPageService.getProductQnADetail(qnaId, fixturePrincipal));

        assertNotNull(result);
        assertEquals(qnaId, result.productQnAId());
        assertEquals(fixtureQnA.getProduct().getProductName(), result.productName());
        assertEquals(fixtureQnA.getMember().getNickname(), result.writer());
        assertEquals(fixtureQnA.getQnaContent(), result.qnaContent());
        assertEquals(fixtureQnA.getCreatedAt().toLocalDate(), result.createdAt());
        assertTrue(result.productQnAStat());
        assertEquals(1, result.replyList().size());

        MyPageQnAReplyDTO resultReplyDTO = result.replyList().get(0);

        assertEquals(fixture.getId(), resultReplyDTO.replyId());
        assertEquals(fixture.getMember().getNickname(), resultReplyDTO.writer());
        assertEquals(fixture.getReplyContent(), resultReplyDTO.replyContent());
        assertEquals(fixture.getUpdatedAt().toLocalDate(), resultReplyDTO.updatedAt());
    }

    @Test
    @DisplayName(value = "상품 문의 상세 조회. 답변이 없는 경우")
    void getProductQnADetailReplyIsNull() {
        ProductQnA fixture = newProductQnAList.get(0);
        Principal fixturePrincipal = () -> fixture.getMember().getUserId();

        ProductQnADetailDTO result = assertDoesNotThrow(() -> myPageService.getProductQnADetail(fixture.getId(), fixturePrincipal));

        assertNotNull(result);
        assertEquals(fixture.getId(), result.productQnAId());
        assertEquals(fixture.getProduct().getProductName(), result.productName());
        assertEquals(fixture.getMember().getNickname(), result.writer());
        assertEquals(fixture.getQnaContent(), result.qnaContent());
        assertEquals(fixture.getCreatedAt().toLocalDate(), result.createdAt());
        assertFalse(result.productQnAStat());
        assertTrue(result.replyList().isEmpty());
    }

    @Test
    @DisplayName(value = "상품 문의 상세 조회. 작성자가 일치하지 않는 경우")
    void getProductQnADetailWriterNotEquals() {
        ProductQnA fixture = newProductQnAList.get(0);
        Principal fixturePrincipal = () -> "noneUserId";

        assertThrows(
                CustomAccessDeniedException.class,
                () -> myPageService.getProductQnADetail(fixture.getId(), fixturePrincipal)
        );
    }

    @Test
    @DisplayName(value = "상품 문의 상세 조회. 데이터가 없는 경우")
    void getProductQnADetailNotFound() {
        assertThrows(
                CustomNotFoundException.class,
                () -> myPageService.getProductQnADetail(0L, principal)
        );
    }

    @Test
    @DisplayName(value = "상품 문의 삭제")
    void deleteProductQnA() {
        ProductQnA fixture = allProductQnAList.get(0);
        Principal fixturePrincipal = () -> fixture.getMember().getUserId();
        long qnaId = fixture.getId();

        String result = assertDoesNotThrow(() -> myPageService.deleteProductQnA(qnaId, fixturePrincipal));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        ProductQnA deleteData = productQnARepository.findById(qnaId).orElse(null);

        assertNull(deleteData);
    }

    @Test
    @DisplayName(value = "상품 문의 삭제. QnAId 가 잘못된 경우")
    void deleteProductQnAWrongId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> myPageService.deleteProductQnA(0L, principal)
        );
    }

    @Test
    @DisplayName(value = "상품 문의 삭제. 작성자가 아닌 경우")
    void deleteProductQnAWriterNotEquals() {
        ProductQnA fixture = allProductQnAList.get(0);
        Principal fixturePrincipal = () -> "noneUserId";
        long qnaId = fixture.getId();

        assertThrows(
                CustomAccessDeniedException.class,
                () -> myPageService.deleteProductQnA(qnaId, fixturePrincipal)
        );

        ProductQnA deleteData = productQnARepository.findById(qnaId).orElse(null);

        assertNotNull(deleteData);
    }

    @Test
    @DisplayName(value = "회원 문의 목록 조회")
    void getMemberQnAList() {
        List<MemberQnA> fixtureList = getMemberMemberQnAList(member, 0);
        MyPagePageDTO pageDTO = PageDTOFixture.createDefaultMyPagePageDTO(1);
        int totalPages = PaginationUtils.getTotalPages(fixtureList.size(), pageDTO.amount());
        int contentElements = Math.min(fixtureList.size(), pageDTO.amount());

        Page<MemberQnAListDTO> result = assertDoesNotThrow(() -> myPageService.getMemberQnAList(pageDTO, principal));

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertFalse(result.isEmpty());
        assertEquals(contentElements, result.getContent().size());
        assertEquals(totalPages, result.getTotalPages());
        assertEquals(fixtureList.size(), result.getTotalElements());
    }

    @Test
    @DisplayName(value = "회원 문의 목록 조회. 데이터가 없는 경우")
    void getMemberQnAListEmpty() {
        memberQnARepository.deleteAll();
        MyPagePageDTO pageDTO = PageDTOFixture.createDefaultMyPagePageDTO(1);

        Page<MemberQnAListDTO> result = assertDoesNotThrow(() -> myPageService.getMemberQnAList(pageDTO, principal));

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalPages());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    @DisplayName(value = "회원 문의 작성")
    void postMemberQnA() {
        QnAClassification qnAClassification = qnAClassificationList.get(0);
        MemberQnAInsertDTO insertDTO = new MemberQnAInsertDTO(
                "testInsertTitle",
                "testInsertContent",
                qnAClassification.getId()
        );

        Long result = assertDoesNotThrow(() -> myPageService.postMemberQnA(insertDTO, principal));

        assertNotNull(result);

        MemberQnA saveData = memberQnARepository.findById(result).orElse(null);

        assertNotNull(saveData);
        assertEquals(principal.getName(), saveData.getMember().getUserId());
        assertEquals(insertDTO.title(), saveData.getMemberQnATitle());
        assertEquals(insertDTO.content(), saveData.getMemberQnAContent());
        assertEquals(qnAClassification.getId(), saveData.getQnAClassification().getId());
    }

    @Test
    @DisplayName(value = "회원 문의 작성. 문의 분류 아이디가 잘못된 경우")
    void postMemberQnAClassificationNotFound() {
        MemberQnAInsertDTO insertDTO = new MemberQnAInsertDTO(
                "testInsertTitle",
                "testInsertContent",
                0L
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> myPageService.postMemberQnA(insertDTO, principal)
        );
    }

    @Test
    @DisplayName(value = "회원 문의 상세 조회")
    void getMemberQnADetail() {
        MemberQnA fixture = answerMemberQnAList.get(0);
        List<MemberQnAReply> replyFixtureList = memberQnAReplyList.stream()
                .filter(v -> v.getMemberQnA().getId().equals(fixture.getId()))
                .toList();
        Principal fixturePrincipal = () -> fixture.getMember().getUserId();

        MemberQnADetailDTO result = assertDoesNotThrow(() -> myPageService.getMemberQnADetail(fixture.getId(), fixturePrincipal));

        assertNotNull(result);
        assertEquals(fixture.getId(), result.memberQnAId());
        assertEquals(fixture.getQnAClassification().getQnaClassificationName(), result.qnaClassification());
        assertEquals(fixture.getMemberQnATitle(), result.qnaTitle());
        assertEquals(fixture.getMember().getNickname(), result.writer());
        assertEquals(fixture.getMemberQnAContent(), result.qnaContent());
        assertEquals(fixture.getUpdatedAt().toLocalDate(), result.updatedAt());
        assertEquals(fixture.isMemberQnAStat(), result.memberQnAStat());
        assertEquals(replyFixtureList.size(), result.replyList().size());
    }

    @Test
    @DisplayName(value = "회원 문의 상세 조회. 작성자가 일치하지 않는 경우")
    void getMemberQnADetailWriterNotEquals() {
        MemberQnA fixture = answerMemberQnAList.get(0);
        Principal fixturePrincipal = () -> "noneUserId";

        assertThrows(
                CustomAccessDeniedException.class,
                () -> myPageService.getMemberQnADetail(fixture.getId(), fixturePrincipal)
        );
    }

    @Test
    @DisplayName(value = "회원 문의 상세 조회. 회원 문의 아이디가 잘못된 경우")
    void getMemberQnADetailWrongId() {
        assertThrows(
                CustomNotFoundException.class,
                () -> myPageService.getMemberQnADetail(0L, principal)
        );
    }

    @Test
    @DisplayName(value = "회원 문의 답변 작성")
    void postMemberQnAReply() {
        MemberQnA fixture = answerMemberQnAList.stream().filter(MemberQnA::isMemberQnAStat).toList().get(0);
        int size = memberQnAReplyList.stream().filter(v -> v.getMemberQnA().getId().equals(fixture.getId())).toList().size();
        Principal fixturePrincipal = () -> fixture.getMember().getUserId();
        QnAReplyInsertDTO replyInsertDTO = new QnAReplyInsertDTO(fixture.getId(), "test reply content");

        String result = assertDoesNotThrow(() -> myPageService.postMemberQnAReply(replyInsertDTO, fixturePrincipal));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        MemberQnA patchData = memberQnARepository.findById(fixture.getId()).orElse(null);
        List<MyPageQnAReplyDTO> replyList = memberQnAReplyRepository.findAllByQnAId(fixture.getId());
        MyPageQnAReplyDTO saveReply = replyList.get(replyList.size() - 1);

        assertNotNull(patchData);
        assertFalse(patchData.isMemberQnAStat());
        assertEquals(size + 1, replyList.size());
        assertEquals(fixture.getMember().getNickname(), saveReply.writer());
        assertEquals(replyInsertDTO.content(), saveReply.replyContent());
    }

    @Test
    @DisplayName(value = "회원 문의 답변 작성. 관리자가 아닌데 문의 작성자와 답글 작성자가 일치하지 않는 경우")
    void postMemberQnAReplyWriterNotEquals() {
        MemberQnA fixture = answerMemberQnAList.stream()
                .filter(v ->
                        v.isMemberQnAStat() && v.getMember().getUserId().equals(member.getUserId())
                )
                .toList()
                .get(0);
        Principal fixturePrincipal = () -> memberList.get(1).getUserId();
        QnAReplyInsertDTO replyInsertDTO = new QnAReplyInsertDTO(fixture.getId(), "test reply content");

        assertThrows(
                CustomAccessDeniedException.class,
                () -> myPageService.postMemberQnAReply(replyInsertDTO, fixturePrincipal)
        );

        MemberQnA patchData = memberQnARepository.findById(fixture.getId()).orElse(null);

        assertNotNull(patchData);
        assertTrue(patchData.isMemberQnAStat());
    }

    @Test
    @DisplayName(value = "회원 문의 답변 작성. 문의 아이디가 잘못 된 경우")
    void postMemberQnAReplyWrongId() {
        QnAReplyInsertDTO replyInsertDTO = new QnAReplyInsertDTO(0L, "test reply content");

        assertThrows(
                IllegalArgumentException.class,
                () -> myPageService.postMemberQnAReply(replyInsertDTO, principal)
        );
    }

    @Test
    @DisplayName(value = "회원 문의 답변 수정")
    void patchMemberQnAReply() {
        MemberQnAReply fixture = memberQnAReplyList.get(0);
        Principal fixturePrincipal = () -> fixture.getMember().getUserId();
        QnAReplyDTO replyDTO = new QnAReplyDTO(fixture.getId(), "test patch reply Content");

        String result = assertDoesNotThrow(() -> myPageService.patchMemberQnAReply(replyDTO, fixturePrincipal));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        MemberQnAReply patchData = memberQnAReplyRepository.findById(fixture.getId()).orElse(null);

        assertNotNull(patchData);
        assertEquals(replyDTO.content(), patchData.getReplyContent());
    }

    @Test
    @DisplayName(value = "회원 문의 답변 수정. 답변 아이디가 잘못 된 경우")
    void patchMemberQnAReplyWrongId() {
        QnAReplyDTO replyDTO = new QnAReplyDTO(0L, "test patch reply Content");

        assertThrows(
                IllegalArgumentException.class,
                () -> myPageService.patchMemberQnAReply(replyDTO, principal)
        );
    }

    @Test
    @DisplayName(value = "회원 문의 답변 수정. 작성자가 일치하지 않는 경우")
    void patchMemberQnAReplyWriterNotEquals() {
        MemberQnAReply fixture = memberQnAReplyList.get(0);
        Principal fixturePrincipal = () -> "noneUser";
        QnAReplyDTO replyDTO = new QnAReplyDTO(fixture.getId(), "test patch reply Content");

        assertThrows(
                CustomAccessDeniedException.class,
                () -> myPageService.patchMemberQnAReply(replyDTO, fixturePrincipal)
        );
    }

    @Test
    @DisplayName(value = "회원 문의 수정을 위한 상세 조회")
    void getModifyData() {
        MemberQnA fixture = allMemberQnAList.get(0);
        Principal fixturePrincipal = () -> fixture.getMember().getUserId();

        MemberQnAModifyDataDTO result = assertDoesNotThrow(() -> myPageService.getModifyData(fixture.getId(), fixturePrincipal));

        assertNotNull(result);
        assertEquals(fixture.getId(), result.qnaId());
        assertEquals(fixture.getMemberQnATitle(), result.qnaTitle());
        assertEquals(fixture.getMemberQnAContent(), result.qnaContent());
        assertEquals(fixture.getQnAClassification().getId(), result.qnaClassificationId());
        assertEquals(qnAClassificationList.size(), result.classificationList().size());
    }

    @Test
    @DisplayName(value = "회원 문의 수정을 위한 상세 조회. 회원 문의 아이디가 잘못 된 경우")
    void getModifyDataWrongId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> myPageService.getModifyData(0L, principal)
        );
    }

    @Test
    @DisplayName(value = "회원 문의 수정")
    void patchMemberQnA() {
        MemberQnA fixture = allMemberQnAList.get(0);
        Principal fixturePrincipal = () -> fixture.getMember().getUserId();
        long classificationId = qnAClassificationList.get(1).getId();
        MemberQnAModifyDTO modifyDTO = new MemberQnAModifyDTO(
                fixture.getId(),
                "test modify title",
                "test modify content",
                classificationId
        );

        String result = assertDoesNotThrow(() -> myPageService.patchMemberQnA(modifyDTO, fixturePrincipal));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        MemberQnA patchData = memberQnARepository.findById(fixture.getId()).orElse(null);

        assertNotNull(patchData);
        assertEquals(modifyDTO.title(), patchData.getMemberQnATitle());
        assertEquals(modifyDTO.content(), patchData.getMemberQnAContent());
        assertEquals(modifyDTO.classificationId(), patchData.getQnAClassification().getId());
    }

    @Test
    @DisplayName(value = "회원 문의 수정. 회원 문의 아이디가 잘못 된 경우")
    void patchMemberQnAWrongId() {
        long classificationId = qnAClassificationList.get(1).getId();
        MemberQnAModifyDTO modifyDTO = new MemberQnAModifyDTO(
                0L,
                "test modify title",
                "test modify content",
                classificationId
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> myPageService.patchMemberQnA(modifyDTO, principal)
        );
    }

    @Test
    @DisplayName(value = "회원 문의 수정. 작성자가 일치하지 않는 경우")
    void patchMemberQnAWriterNotEquals() {
        MemberQnA fixture = allMemberQnAList.get(0);
        Principal fixturePrincipal = () -> memberList.get(1).getUserId();
        long classificationId = qnAClassificationList.get(1).getId();
        MemberQnAModifyDTO modifyDTO = new MemberQnAModifyDTO(
                fixture.getId(),
                "test modify title",
                "test modify content",
                classificationId
        );

        assertThrows(
                CustomAccessDeniedException.class,
                () -> myPageService.patchMemberQnA(modifyDTO, fixturePrincipal)
        );
    }

    @Test
    @DisplayName(value = "회원 문의 삭제")
    void deleteMemberQnA() {
        MemberQnA fixture = allMemberQnAList.get(0);
        Principal fixturePrincipal = () -> fixture.getMember().getUserId();

        String result = assertDoesNotThrow(() -> myPageService.deleteMemberQnA(fixture.getId(), fixturePrincipal));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        MemberQnA deleteData = memberQnARepository.findById(fixture.getId()).orElse(null);
        assertNull(deleteData);
    }

    @Test
    @DisplayName(value = "회원 문의 삭제. 잘못 된 아이디인 경우")
    void deleteMemberQnAWrongId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> myPageService.deleteMemberQnA(0L, principal)
        );
    }

    @Test
    @DisplayName(value = "회원 문의 삭제. 작성자가 일치하지 않는 경우")
    void deleteMemberQnAWriterNotEquals() {
        MemberQnA fixture = allMemberQnAList.get(0);
        Principal fixturePrincipal = () -> memberList.get(1).getUserId();

        assertThrows(
                CustomAccessDeniedException.class,
                () -> myPageService.deleteMemberQnA(fixture.getId(), fixturePrincipal)
        );

        MemberQnA deleteData = memberQnARepository.findById(fixture.getId()).orElse(null);

        assertNotNull(deleteData);
    }

    @Test
    @DisplayName(value = "회원 문의 분류 목록 조회")
    void getQnAClassification() {
        List<QnAClassificationDTO> result = assertDoesNotThrow(() -> myPageService.getQnAClassification(principal));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(qnAClassificationList.size(), result.size());
        for(int i = 0; i < qnAClassificationList.size(); i++) {
            QnAClassification classification = qnAClassificationList.get(i);
            QnAClassificationDTO resultDTO = result.get(i);

            assertEquals(classification.getId(), resultDTO.id());
            assertEquals(classification.getQnaClassificationName(), resultDTO.name());
        }
    }

    @Test
    @DisplayName(value = "회원 문의 분류 목록 조회. 데이터가 없는 경우")
    void getQnAClassificationEmpty() {
        qnAClassificationRepository.deleteAll();

        List<QnAClassificationDTO> result = assertDoesNotThrow(() -> myPageService.getQnAClassification(principal));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName(value = "리뷰 목록 조회")
    void getReview() {
        List<ProductReview> fixtureList = getMemberProductReviewList(member, 0);
        MyPagePageDTO pageDTO = PageDTOFixture.createDefaultMyPagePageDTO(1);
        int totalPages = PaginationUtils.getTotalPages(fixtureList.size(), pageDTO.amount());
        int contentSize = Math.min(fixtureList.size(), pageDTO.amount());

        Page<MyPageReviewDTO> result = assertDoesNotThrow(() -> myPageService.getReview(pageDTO, principal));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertFalse(result.getContent().isEmpty());
        assertEquals(fixtureList.size(), result.getTotalElements());
        assertEquals(totalPages, result.getTotalPages());
        assertEquals(contentSize, result.getContent().size());
    }

    @Test
    @DisplayName(value = "리뷰 목록 조회. 데이터가 없는 경우")
    void getReviewEmpty() {
        productReviewRepository.deleteAll();
        MyPagePageDTO pageDTO = PageDTOFixture.createDefaultMyPagePageDTO(1);

        Page<MyPageReviewDTO> result = assertDoesNotThrow(() -> myPageService.getReview(pageDTO, principal));

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    @DisplayName(value = "리뷰 수정을 위한 조회")
    void getPatchReview() {
        ProductReview fixture = allProductReviewList.get(0);
        Principal fixturePrincipal = () -> fixture.getMember().getUserId();

        MyPagePatchReviewDataDTO result = assertDoesNotThrow(() -> myPageService.getPatchReview(fixture.getId(), fixturePrincipal));

        assertNotNull(result);
        assertEquals(fixture.getId(), result.reviewId());
        assertEquals(fixture.getReviewContent(), result.content());
        assertEquals(fixture.getProduct().getProductName(), result.productName());
    }

    @Test
    @DisplayName(value = "리뷰 수정을 위한 조회. 리뷰 아이디가 잘못 된 경우")
    void getPatchReviewWrongId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> myPageService.getPatchReview(0L, principal)
        );
    }

    @Test
    @DisplayName(value = "리뷰 수정을 위한 조회. 작성자가 일치하지 않는 경우")
    void getPatchReviewWriterNotEquals() {
        ProductReview fixture = allProductReviewList.get(0);
        Principal fixturePrincipal = () -> "noneUser";

        assertThrows(
                CustomAccessDeniedException.class,
                () -> myPageService.getPatchReview(fixture.getId(), fixturePrincipal)
        );
    }

    @Test
    @DisplayName(value = "리뷰 작성")
    void postReview() {
        ProductOrderDetail fixture = productOrderList.stream()
                .flatMap(v -> v.getProductOrderDetailSet().stream())
                .filter(v -> !v.isOrderReviewStatus())
                .toList()
                .get(0);
        Principal fixturePrincipal = () -> fixture.getProductOrder().getMember().getUserId();
        MyPagePostReviewDTO postReviewDTO = new MyPagePostReviewDTO(
                fixture.getProduct().getId(),
                "test post review content",
                fixture.getProductOption().getId(),
                fixture.getId()
        );

        String result = assertDoesNotThrow(() -> myPageService.postReview(postReviewDTO, fixturePrincipal));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        ProductReview postReview = productReviewRepository.findFirstByMember_UserIdOrderByIdDesc(principal.getName());

        assertNotNull(postReview);
        assertEquals(postReviewDTO.productId(), postReview.getProduct().getId());
        assertEquals(postReviewDTO.content(), postReview.getReviewContent());
        assertEquals(postReviewDTO.optionId(), postReview.getProductOption().getId());
        assertEquals(principal.getName(), postReview.getMember().getUserId());

        ProductOrderDetail patchDetail = productOrderDetailRepository.findById(fixture.getId()).orElse(null);

        assertNotNull(patchDetail);
        assertTrue(patchDetail.isOrderReviewStatus());
    }

    @Test
    @DisplayName(value = "리뷰 작성. 상품 아이디가 잘못 된 경우")
    void postReviewWrongProductId() {
        MyPagePostReviewDTO postReviewDTO = new MyPagePostReviewDTO(
                "noneProductId",
                "test post review content",
                productOptionList.get(0).getId(),
                productOrderList.get(0).getProductOrderDetailSet().get(0).getId()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> myPageService.postReview(postReviewDTO, principal)
        );

        int allReviewSize = productReviewRepository.findAll().size();

        assertEquals(allProductReviewList.size(), allReviewSize);
    }

    @Test
    @DisplayName(value = "리뷰 작성. 상품 옵션 아이디가 잘못 된 경우")
    void postReviewWrongProductOptionId() {
        MyPagePostReviewDTO postReviewDTO = new MyPagePostReviewDTO(
                productList.get(0).getId(),
                "test post review content",
                0L,
                productOrderList.get(0).getProductOrderDetailSet().get(0).getId()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> myPageService.postReview(postReviewDTO, principal)
        );

        int allReviewSize = productReviewRepository.findAll().size();

        assertEquals(allProductReviewList.size(), allReviewSize);
    }

    @Test
    @DisplayName(value = "리뷰 작성. 주문 상세 아이디가 잘못 된 경우")
    void postReviewWrongProductOrderDetailId() {
        MyPagePostReviewDTO postReviewDTO = new MyPagePostReviewDTO(
                productList.get(0).getId(),
                "test post review content",
                productOptionList.get(0).getId(),
                0L
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> myPageService.postReview(postReviewDTO, principal)
        );

        int allReviewSize = productReviewRepository.findAll().size();

        assertEquals(allProductReviewList.size(), allReviewSize);
    }

    @Test
    @DisplayName(value = "리뷰 수정")
    void patchReview() {
        ProductReview fixture = allProductReviewList.get(0);
        Principal fixturePrincipal = () -> fixture.getMember().getUserId();
        MyPagePatchReviewDTO reviewDTO = new MyPagePatchReviewDTO(
                fixture.getId(),
                "test patch review content"
        );

        String result = assertDoesNotThrow(() -> myPageService.patchReview(reviewDTO, fixturePrincipal));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        ProductReview patchReview = productReviewRepository.findById(fixture.getId()).orElse(null);

        assertNotNull(patchReview);
        assertEquals(reviewDTO.content(), patchReview.getReviewContent());
    }

    @Test
    @DisplayName(value = "리뷰 수정. 리뷰 아이디가 잘못 된 경우")
    void patchReviewWrongId() {
        MyPagePatchReviewDTO reviewDTO = new MyPagePatchReviewDTO(
                0L,
                "test patch review content"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> myPageService.patchReview(reviewDTO, principal)
        );
    }

    @Test
    @DisplayName(value = "리뷰 수정. 작성자가 일치하지 않는 경우")
    void patchReviewWriterNotEquals() {
        ProductReview fixture = allProductReviewList.get(0);
        Principal fixturePrincipal = () -> "noneUser";
        MyPagePatchReviewDTO reviewDTO = new MyPagePatchReviewDTO(
                fixture.getId(),
                "test patch review content"
        );

        assertThrows(
                CustomAccessDeniedException.class,
                () -> myPageService.patchReview(reviewDTO, fixturePrincipal)
        );
    }

    @Test
    @DisplayName(value = "리뷰 삭제")
    void deleteReview() {
        ProductReview fixture = allProductReviewList.get(0);
        long reviewId = fixture.getId();
        Principal fixturePrincipal = () -> fixture.getMember().getUserId();

        String result = assertDoesNotThrow(() -> myPageService.deleteReview(reviewId, fixturePrincipal));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        ProductReview deleteReview = productReviewRepository.findById(reviewId).orElse(null);

        assertNull(deleteReview);
    }

    @Test
    @DisplayName(value = "리뷰 삭제. 리뷰 아이디가 잘못 된 경우")
    void deleteReviewWrongId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> myPageService.deleteReview(0L, principal)
        );
    }

    @Test
    @DisplayName(value = "리뷰 삭제. 작성자가 일치하지 않는 경우")
    void deleteReviewWriterNotEquals() {
        ProductReview fixture = allProductReviewList.get(0);
        long reviewId = fixture.getId();
        Principal fixturePrincipal = () -> "noneUser";

        assertThrows(
                CustomAccessDeniedException.class,
                () -> myPageService.deleteReview(reviewId, fixturePrincipal)
        );

        ProductReview deleteReview = productReviewRepository.findById(reviewId).orElse(null);

        assertNotNull(deleteReview);
    }

    @Test
    @DisplayName(value = "사용자 정보 조회")
    void getInfo() {
        MyPageInfoDTO result = assertDoesNotThrow(() -> myPageService.getInfo(principal));
        String[] splitMail = member.getUserEmail().split("@");
        String mailSuffix = splitMail[1].substring(0, splitMail[1].indexOf('.'));
        String type = MailSuffix.findSuffixType(mailSuffix);

        assertNotNull(result);
        assertEquals(member.getNickname(), result.nickname());
        assertEquals(member.getPhone().replaceAll("-", ""), result.phone());
        assertEquals(splitMail[0], result.mailPrefix());
        assertEquals(splitMail[1], result.mailSuffix());
        assertEquals(type, result.mailType());
    }

    @Test
    @DisplayName(value = "사용자 정보 수정")
    void patchInfo() {
        MyPageInfoPatchDTO infoDTO = new MyPageInfoPatchDTO(
                "modifyNickname",
                "01099998888",
                "modify@modify.com"
        );

        String result = assertDoesNotThrow(() -> myPageService.patchInfo(infoDTO, principal));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        Member patchMember = memberRepository.findByUserId(member.getUserId());

        assertEquals(infoDTO.nickname(), patchMember.getNickname());
        assertEquals(infoDTO.phone(), patchMember.getPhone().replaceAll("-", ""));
        assertEquals(infoDTO.mail(), patchMember.getUserEmail());
    }
}
