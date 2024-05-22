package com.example.mansshop_boot.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .headers(config ->
                        config.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                );

        http
                .authorizeHttpRequests(registry ->
                        registry.requestMatchers("/login/**", "/api/**")
                                .permitAll()
                );

        http
                .formLogin(configurer ->
                configurer.usernameParameter("userId")
                        .passwordParameter("userPw")
                        .successHandler(authenticationSuccessHandler)
                        .loginProcessingUrl("/api/login")
        );

        http
                .logout(configurer ->
                        configurer.logoutRequestMatcher(new AntPathRequestMatcher("/api/logout"))
                                .logoutSuccessUrl("/")
                                .invalidateHttpSession(true)
                );

        /*http
                .oauth2Login((oauth2) ->
                        oauth2
                                .userInfoEndpoint((userInfoEndpointConfig) ->
                                        userInfoEndpointConfig
                                                .userService())
                                .successHandler());*/

        return http.build();
    }
}
