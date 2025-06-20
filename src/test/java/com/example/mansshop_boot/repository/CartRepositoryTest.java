package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.Fixture.CartFixture;
import com.example.mansshop_boot.Fixture.ClassificationFixture;
import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.cart.CartDetailRepository;
import com.example.mansshop_boot.repository.cart.CartRepository;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = MansShopBootApplication.class)
@ActiveProfiles("test")
public class CartRepositoryTest {

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

    private Cart anonymousCart;

    @BeforeAll
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

        for(Cart c : saveList) {
            if(c.getCookieId() != null) {
                anonymousCart = c;
                break;
            }
        }
    }

    @Test
    @DisplayName(value = "사용자 아이디를 통한 Cart 조회")
    void findCartByUserId() {
        CartMemberDTO cartMemberDTO = new CartMemberDTO(member.getUserId(), null);
        Cart result = cartRepository.findByUserIdAndCookieValue(cartMemberDTO);

        assertNotNull(result);
        assertEquals(memberCart.getId(), result.getId());
        assertNull(result.getCookieId());
    }

    @Test
    @DisplayName(value = "쿠키값을 통한 Cart 조회")
    void findCartByCookieId() {
        String anonymous = "Anonymous";
        CartMemberDTO cartMemberDTO = new CartMemberDTO(anonymous, anonymousCart.getCookieId());
        Cart result = cartRepository.findByUserIdAndCookieValue(cartMemberDTO);

        assertNotNull(result);
        assertEquals(anonymousCart.getId(), result.getId());
        assertEquals(anonymousCart.getCookieId(), result.getCookieId());
    }

    @Test
    @DisplayName(value = "사용자 아이디를 통한 CartId 조회")
    void findCartIdByUserId() {
        CartMemberDTO cartMemberDTO = new CartMemberDTO(member.getUserId(), null);
        Long result = cartRepository.findIdByUserId(cartMemberDTO);

        assertNotNull(result);
        assertEquals(memberCart.getId(), result);
    }

    @Test
    @DisplayName(value = "쿠키값을 통한 CartId 조회")
    void findCartIdByCookieId() {
        String anonymous = "Anonymous";
        CartMemberDTO cartMemberDTO = new CartMemberDTO(anonymous, anonymousCart.getCookieId());
        Long result = cartRepository.findIdByUserId(cartMemberDTO);

        assertNotNull(result);
        assertEquals(anonymousCart.getId(), result);
    }
}
