package com.example.mansshop_boot.service.integration;

import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
@ActiveProfiles("test")
@Transactional
public class MemberServiceIT {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void init() {
        MemberAndAuthFixtureDTO memberAndAuthFixture = MemberAndAuthFixture.createDefaultMember(1);

        memberRepository.saveAll(memberAndAuthFixture.memberList());
        authRepository.saveAll(memberAndAuthFixture.authList());
    }
}
