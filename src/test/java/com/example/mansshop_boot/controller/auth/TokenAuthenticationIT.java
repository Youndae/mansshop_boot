package com.example.mansshop_boot.controller.auth;

import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.ExceptionEntity;
import com.example.mansshop_boot.controller.fixture.TokenFixture;
import com.example.mansshop_boot.domain.entity.Auth;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * 토큰 검증 및 메서드 접근 제어에 대한 통합 테스트
 */
@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class TokenAuthenticationIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EntityManager em;

    @Autowired
    private TokenFixture tokenFixture;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthRepository authRepository;

    @Value("#{jwt['token.access.header']}")
    private String accessHeader;

    @Value("#{jwt['token.refresh.header']}")
    private String refreshHeader;

    @Value("#{jwt['cookie.ino.header']}")
    private String inoHeader;

    @Value("#{jwt['token.all.prefix']}")
    private String tokenPrefix;

    private Map<String, String> memberTokenMap;

    private String memberAccessTokenValue;

    private String memberRefreshTokenValue;

    private String memberInoValue;

    private Map<String, String> adminTokenMap;

    private String adminAccessTokenValue;

    private String adminRefreshTokenValue;

    private String adminInoValue;

    private String expirationTestToken;

    private Member member;

    private static final String WRONG_ACCESS_TOKEN = "wrongAccessTokenValue";

    @BeforeEach
    void init() {
        MemberAndAuthFixtureDTO memberAndAuthFixture = MemberAndAuthFixture.createDefaultMember(1);
        member = memberAndAuthFixture.memberList().get(0);
        MemberAndAuthFixtureDTO adminFixture = MemberAndAuthFixture.createAdmin();
        Member admin = adminFixture.memberList().get(0);

        List<Member> saveMemberList = new ArrayList<>();
        saveMemberList.add(member);
        saveMemberList.add(admin);

        List<Auth> saveAuthList = new ArrayList<>(memberAndAuthFixture.authList());
        saveAuthList.addAll(adminFixture.authList());

        memberRepository.saveAll(saveMemberList);
        authRepository.saveAll(saveAuthList);

        memberTokenMap = tokenFixture.createAndSaveAllToken(member);
        memberAccessTokenValue = memberTokenMap.get(accessHeader);
        memberRefreshTokenValue = memberTokenMap.get(refreshHeader);
        memberInoValue = memberTokenMap.get(inoHeader);

        adminTokenMap = tokenFixture.createAndSaveAllToken(admin);
        adminAccessTokenValue = adminTokenMap.get(accessHeader);
        adminRefreshTokenValue = adminTokenMap.get(refreshHeader);
        adminInoValue = adminTokenMap.get(inoHeader);

        expirationTestToken = tokenFixture.createExpirationToken(member);

        em.flush();
        em.clear();
    }

    @AfterEach
    void cleanUp() {
        String memberAccessKey = memberTokenMap.get("accessKey");
        String memberRefreshKey = memberTokenMap.get("refreshKey");
        String adminAccessKey = adminTokenMap.get("accessKey");
        String adminRefreshKey = adminTokenMap.get("refreshKey");

        redisTemplate.delete(memberAccessKey);
        redisTemplate.delete(memberRefreshKey);
        redisTemplate.delete(adminAccessKey);
        redisTemplate.delete(adminRefreshKey);
    }

    @Test
    @DisplayName(value = "권한 제어가 없는 컨트롤러로 비회원 접근")
    void anonymousRequest() throws Exception {
        mockMvc.perform(get("/api/main/"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName(value = "토큰 검증 정상 통과")
    void verifyToken() throws Exception {
        mockMvc.perform(get("/api/my-page/like")
                .header(accessHeader, memberAccessTokenValue)
                .cookie(new Cookie(refreshHeader, memberRefreshTokenValue))
                .cookie(new Cookie(inoHeader, memberInoValue)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName(value = "AccessToken이 잘못된 경우")
    void wrongAccessToken() throws Exception {
        String wrongToken = tokenPrefix + WRONG_ACCESS_TOKEN;
        mockMvc.perform(get("/api/my-page/like")
                        .header(accessHeader, wrongToken)
                        .cookie(new Cookie(refreshHeader, memberRefreshTokenValue))
                        .cookie(new Cookie(inoHeader, memberInoValue)))
                .andExpect(status().is(800))
                .andReturn();
    }

    @Test
    @DisplayName(value = "AccessToken이 탈취로 판단된 경우")
    void stealingAccessToken() throws Exception {
        Thread.sleep(1000);
        String stealingTestToken = tokenFixture.createAccessToken(member);

        mockMvc.perform(get("/api/my-page/like")
                        .header(accessHeader, stealingTestToken)
                        .cookie(new Cookie(refreshHeader, memberRefreshTokenValue))
                        .cookie(new Cookie(inoHeader, memberInoValue)))
                .andExpect(status().is(800))
                .andReturn();
    }

    @Test
    @DisplayName(value = "AccessToken이 만료된 경우")
    void expirationAccessToken() throws Exception {
        mockMvc.perform(get("/api/my-page/like")
                        .header(accessHeader, expirationTestToken)
                        .cookie(new Cookie(refreshHeader, memberRefreshTokenValue))
                        .cookie(new Cookie(inoHeader, memberInoValue)))
                .andExpect(status().is(401))
                .andReturn();
    }

    @Test
    @DisplayName(value = "ino가 없는 경우 권한이 필요한 컨트롤러 접근시 403 발생")
    void notExistIno403() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/my-page/like")
                        .header(accessHeader, memberAccessTokenValue)
                        .cookie(new Cookie(refreshHeader, memberRefreshTokenValue)))
                .andExpect(status().is(403))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.ACCESS_DENIED.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "ino가 없는 경우 권한이 필요없는 컨트롤러 접근시 정상 동작")
    void notExistIno() throws Exception {
        mockMvc.perform(get("/api/main/")
                        .header(accessHeader, memberAccessTokenValue)
                        .cookie(new Cookie(refreshHeader, memberRefreshTokenValue)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName(value = "refreshToken만 없는 경우")
    void notExistRefreshToken() throws Exception {
        mockMvc.perform(get("/api/my-page/like")
                        .header(accessHeader, memberAccessTokenValue)
                        .cookie(new Cookie(inoHeader, memberInoValue)))
                .andExpect(status().is(800))
                .andReturn();
    }

    @Test
    @DisplayName(value = "ino만 존재하는 경우 권한이 필요한 컨트롤러 접근 시 403 발생")
    void onlyExistIno403() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/my-page/like")
                        .cookie(new Cookie(inoHeader, memberInoValue)))
                .andExpect(status().is(403))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.ACCESS_DENIED.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "ino만 존재하는 경우 권한이 필요하지 않은 컨트롤러 접근 시 정상처리")
    void onlyExistIno() throws Exception {
        mockMvc.perform(get("/api/main/")
                        .cookie(new Cookie(inoHeader, memberInoValue)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName(value = "토큰의 prefix가 일치하지 않는 경우 권한 필요한 컨트롤러 접근 시 403 발생")
    void wrongTokenPrefix403() throws Exception {
        String removePrefixToken = memberAccessTokenValue.replace(tokenPrefix, "");
        MvcResult result = mockMvc.perform(get("/api/my-page/like")
                        .header(accessHeader, removePrefixToken)
                        .cookie(new Cookie(refreshHeader, memberRefreshTokenValue))
                        .cookie(new Cookie(inoHeader, memberInoValue)))
                .andExpect(status().is(403))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.ACCESS_DENIED.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "토큰의 prefix가 일치하지 않는 경우 권한이 필요하지 않은 컨트롤러 접근 시 정상 동작")
    void wrongTokenPrefix() throws Exception {
        String removePrefixToken = memberAccessTokenValue.replace(tokenPrefix, "");
        mockMvc.perform(get("/api/main/")
                        .header(accessHeader, removePrefixToken)
                        .cookie(new Cookie(refreshHeader, memberRefreshTokenValue))
                        .cookie(new Cookie(inoHeader, memberInoValue)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName(value = "회원이 관리자 컨트롤러에 접근하는 경우")
    void userRequestByAdminController() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/product")
                        .header(accessHeader, memberAccessTokenValue)
                        .cookie(new Cookie(refreshHeader, memberRefreshTokenValue))
                        .cookie(new Cookie(inoHeader, memberInoValue)))
                .andExpect(status().is(403))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.ACCESS_DENIED.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "비회원이 회원 컨트롤러에 접근하는 경우")
    void anonymousRequestByUserController() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/my-page/like"))
                .andExpect(status().is(403))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.ACCESS_DENIED.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "관리자가 회원 컨트롤러에 접근하는 경우")
    void AdminRequestByUserController() throws Exception {
        mockMvc.perform(get("/api/my-page/like")
                        .header(accessHeader, adminAccessTokenValue)
                        .cookie(new Cookie(refreshHeader, adminRefreshTokenValue))
                        .cookie(new Cookie(inoHeader, adminInoValue)))
                .andExpect(status().isOk())
                .andReturn();
    }
}
