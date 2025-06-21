package com.example.mansshop_boot.controller.fixture;

import com.example.mansshop_boot.config.jwt.JWTTokenProvider;
import com.example.mansshop_boot.domain.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TokenFixture {

    @Autowired
    private JWTTokenProvider tokenProvider;

    @Value("#{jwt['token.access.secret']}")
    private String accessSecret;

    @Value("#{jwt['token.access.expiration']}")
    private Long accessExpiration;

    @Value("#{jwt['redis.accessExpiration']}")
    private Long redisAtExpiration;

    @Value("#{jwt['token.access.header']}")
    private String accessHeader;

    @Value("#{jwt['token.refresh.secret']}")
    private String refreshSecret;

    @Value("#{jwt['token.refresh.expiration']}")
    private Long refreshExpiration;

    @Value("#{jwt['redis.refreshExpiration']}")
    private Long redisRtExpiration;

    @Value("#{jwt['token.refresh.header']}")
    private String refreshHeader;

    @Value("#{jwt['cookie.ino.header']}")
    private String inoHeader;

    @Value("#{jwt['redis.accessPrefix']}")
    private String redisAccessPrefix;

    @Value("#{jwt['redis.refreshPrefix']}")
    private String redisRefreshPrefix;

    @Value("#{jwt['token.all.prefix']}")
    private String tokenPrefix;

    public Map<String, String> createAndSaveAllToken(Member member) {
        Map<String, String> tokenMap = new HashMap<>();
        String ino = tokenProvider.createIno();
        String accessToken = tokenProvider.createToken(member.getUserId(), accessSecret, accessExpiration);
        String refreshToken = tokenProvider.createToken(member.getUserId(), refreshSecret, refreshExpiration);

        String accessKey = tokenProvider.setRedisKey(redisAccessPrefix, ino, member.getUserId());
        String refreshKey = tokenProvider.setRedisKey(redisRefreshPrefix, ino, member.getUserId());

        tokenProvider.saveTokenToRedis(accessKey, accessToken, Duration.ofHours(redisAtExpiration));
        tokenProvider.saveTokenToRedis(refreshKey, accessToken, Duration.ofDays(redisRtExpiration));

        tokenMap.put(inoHeader, ino);
        tokenMap.put(accessHeader, tokenPrefix + accessToken);
        tokenMap.put(refreshHeader, tokenPrefix + refreshToken);
        tokenMap.put("accessKey", accessKey);
        tokenMap.put("refreshKey", refreshKey);

        return tokenMap;
    }
}
