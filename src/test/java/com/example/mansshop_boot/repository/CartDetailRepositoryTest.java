package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.Fixture.CartFixture;
import com.example.mansshop_boot.Fixture.ClassificationFixture;
import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.cart.out.CartDetailDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.cart.CartDetailRepository;
import com.example.mansshop_boot.repository.cart.CartRepository;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MansShopBootApplication.class)
@ActiveProfiles("test")
@Transactional
public class CartDetailRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

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
    private CartDetailRepository cartDetailRepository;

    private Member member;

    private Cart memberCart;

    @BeforeEach
    void init() {
        List<Classification> classifications = ClassificationFixture.createClassification();
        List<Product> productList = ProductFixture.createDefaultProductByOUTER(3);
        MemberAndAuthFixtureDTO memberFixture = MemberAndAuthFixture.createDefaultMember(2);
        List<ProductOption> optionList = productList.stream().flatMap(v -> v.getProductOptions().stream()).toList();
        List<Member> memberList = memberFixture.memberList();
        List<Auth> authList = memberFixture.authList();
        MemberAndAuthFixtureDTO anonymousFixture = MemberAndAuthFixture.createAnonymous();
        memberList.addAll(anonymousFixture.memberList());
        authList.addAll(anonymousFixture.authList());
        
        classificationRepository.saveAll(classifications);
        productRepository.saveAll(productList);
        productOptionRepository.saveAll(optionList);
        memberRepository.saveAll(memberList);
        authRepository.saveAll(authList);

        List<Cart> cartList = CartFixture.createDefaultMemberCart(memberList, optionList);
        List<Cart> anonymousCartList = CartFixture.createDefaultAnonymousCart(optionList, 3);

        List<Cart> saveList = new ArrayList<>();
        saveList.addAll(cartList);
        saveList.addAll(anonymousCartList);

        List<CartDetail> cartDetailList = cartList.stream().flatMap(v -> v.getCartDetailList().stream()).toList();

        cartRepository.saveAll(saveList);
        cartDetailRepository.saveAll(cartDetailList);

        member = memberList.get(0);

        for (Cart c : saveList){
            if (c.getMember().getUserId().equals(member.getUserId())) {
                memberCart = c;
                break;
            }
        }
    }

    @Test
    @DisplayName(value = "cartId를 통한 CartDetailDTO List 조회")
    void findCartByUserId() {
        List<CartDetailDTO> result = cartDetailRepository.findAllByCartId(memberCart.getId());

        assertNotNull(result);
        assertEquals(memberCart.getCartDetailList().size(), result.size());
    }

    @Test
    @DisplayName(value = "cartId를 통한 CartDetailId 리스트 조회")
    void countByCartId() {
        List<Long> result = cartDetailRepository.findAllIdByCartId(memberCart.getId());

        assertFalse(result.isEmpty());
        assertEquals(memberCart.getCartDetailList().size(), result.size());
    }

    @Test
    @DisplayName(value = "cartId를 통한 CartDetail List 조회.")
    void findAllCartDetailByCartId() {
        List<CartDetail> result = cartDetailRepository.findAllCartDetailByCartId(memberCart.getId());

        assertNotNull(result);
        assertNotEquals(0L, result.size());
        assertEquals(memberCart.getCartDetailList().size(), result.size());
    }

    @Test
    @DisplayName(value = "cartId와 ProductOptionId List에 해당하는 CartDetail 데이터 조회")
    void findAllCartDetailByCartIdAndOptionIds() {
        List<Long> optionIds = memberCart.getCartDetailList()
                                        .stream()
                                        .map(v -> v.getProductOption().getId())
                                        .toList();

        List<CartDetail> result = cartDetailRepository.findAllCartDetailByCartIdAndOptionIds(memberCart.getId(), optionIds);

        assertNotNull(result);
        assertNotEquals(0L, result.size());
        assertEquals(memberCart.getCartDetailList().size(), result.size());
    }
}
