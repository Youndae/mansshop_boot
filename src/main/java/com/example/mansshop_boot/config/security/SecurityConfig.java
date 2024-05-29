package com.example.mansshop_boot.config.security;

import com.example.mansshop_boot.config.JWTAuthorizationFilter;
import com.example.mansshop_boot.config.oAuth.CustomOAuth2SuccessHandler;
import com.example.mansshop_boot.config.oAuth.CustomOAuth2UserService;
import com.example.mansshop_boot.repository.MemberRepository;
import com.example.mansshop_boot.config.jwt.JWTTokenProvider;
import com.example.mansshop_boot.service.JWTTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsFilter corsFilter;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    private final JWTAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable);

        http
                .sessionManagement(config ->
                        config.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilter(corsFilter)
                .addFilterBefore(
                        jwtAuthorizationFilter
                        , BasicAuthenticationFilter.class
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry ->
                        registry.requestMatchers("/login/**", "/api/**", "/**")
                                .permitAll()
                );

        http
                .oauth2Login((oauth2) ->
                        oauth2
                                .loginPage("/login")
                                .userInfoEndpoint((userInfoEndpointConfig) ->
                                        userInfoEndpointConfig
                                                .userService(customOAuth2UserService))
                                .successHandler(customOAuth2SuccessHandler)
                );

        return http.build();
    }
}
