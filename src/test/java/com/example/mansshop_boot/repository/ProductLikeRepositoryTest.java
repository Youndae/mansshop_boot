package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.Fixture.ClassificationFixture;
import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.Fixture.ProductLikeFixture;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.mypage.out.ProductLikeDTO;
import com.example.mansshop_boot.domain.dto.pageable.LikePageDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.productLike.ProductLikeRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = MansShopBootApplication.class)
@ActiveProfiles("test")
public class ProductLikeRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private ClassificationRepository classificationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private ProductLikeRepository productLikeRepository;

    private static final int PRODUCT_SIZE = 30;

    private ProductLike productLike;

    private List<ProductLike> productLikeList;

    @BeforeAll
    void init(){
        MemberAndAuthFixtureDTO memberAndAuthFixture = MemberAndAuthFixture.createDefaultMember(5);
        List<Classification> classificationList = ClassificationFixture.createClassification();
        List<Product> productFixtureList = ProductFixture.createDefaultProductByOUTER(PRODUCT_SIZE);
        List<ProductOption> optionFixtureList = productFixtureList.stream().flatMap(v -> v.getProductOptions().stream()).toList();
        List<Member> memberFixtureList = memberAndAuthFixture.memberList();

        memberRepository.saveAll(memberFixtureList);
        authRepository.saveAll(memberAndAuthFixture.authList());
        classificationRepository.saveAll(classificationList);
        productRepository.saveAll(productFixtureList);
        productOptionRepository.saveAll(optionFixtureList);

        List<ProductLike> productLikeFixtureList = ProductLikeFixture.createDefaultProductLike(memberFixtureList, productFixtureList);

        productLikeRepository.saveAll(productLikeFixtureList);

        productLike = productLikeFixtureList.get(0);
        productLikeList = productLikeFixtureList;
    }

    @Test
    @DisplayName(value = "사용자 아이디와 상품 아이디 기반 count")
    void countByUserIdAndProductId() {
        int result = productLikeRepository.countByUserIdAndProductId(productLike.getMember().getUserId(), productLike.getProduct().getId());

        Assertions.assertEquals(1, result);
    }

    @Test
    @DisplayName(value = "사용자 아이디와 상품 아이디 기반 count. 데이터가 없는 경우 0을 반환")
    void countByUserIdAndProductIdEmpty() {
        int result = productLikeRepository.countByUserIdAndProductId("fakeUser", "fakeProductId");

        Assertions.assertEquals(0, result);
    }

    @Test
    @DisplayName(value = "관심상품 데이터 제거")
    @Transactional
    void deleteByUserIdAndProductId() {
        Assertions.assertDoesNotThrow(() -> productLikeRepository.deleteByUserIdAndProductId(productLike));

        ProductLike deleteEntity = productLikeRepository.findById(productLike.getId()).orElse(null);
        Assertions.assertNull(deleteEntity);
    }

    @Test
    @DisplayName(value = "사용자 아이디 기반 리스트 검색")
    void findByUserId() {
        LikePageDTO pageDTO = new LikePageDTO(1);
        String userId = productLike.getMember().getUserId();
        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                                            , pageDTO.likeAmount()
                                            , Sort.by("createdAt").descending());
        Page<ProductLikeDTO> result = productLikeRepository.findByUserId(
                userId,
                pageable
        );

        List<ProductLike> memberProductLikeList = productLikeList.stream()
                                                                .filter(v ->
                                                                        v.getMember().getUserId().equals(userId)
                                                                )
                                                                .toList();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(pageDTO.likeAmount(), result.getContent().size());
        Assertions.assertEquals(memberProductLikeList.size(), result.getTotalElements());

        for(ProductLikeDTO resultData : result) {
            boolean flag = false;
            for(ProductLike data : memberProductLikeList) {
                if(resultData.likeId() == data.getId()){
                    Assertions.assertEquals(resultData.productId(), data.getProduct().getId());
                    flag = true;
                    break;
                }
            }
            Assertions.assertTrue(flag);
        }
    }
}
