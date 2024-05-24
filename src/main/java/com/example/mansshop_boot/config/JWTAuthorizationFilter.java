package com.example.mansshop_boot.config;

import com.example.mansshop_boot.repository.MemberRepository;
import com.example.mansshop_boot.service.jwt.JWTTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final MemberRepository memberRepository;

    private final JWTTokenProvider jwtTokenProvider;

    public JWTAuthorizationFilter(MemberRepository memberRepository, JWTTokenProvider tokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = tokenProvider;
    }

    @Value("#{jwt['token.all.prefix']}")
    private String tokenPrefix;

    @Value("#{jwt['token.access.header']}")
    private String accessHeader;

    @Value("#{jwt['token.refresh.header']}")
    private String refreshHeader;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    }
}
