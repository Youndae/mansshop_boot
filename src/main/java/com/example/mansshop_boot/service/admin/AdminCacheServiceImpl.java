package com.example.mansshop_boot.service.admin;

import com.example.mansshop_boot.domain.dto.cache.CacheProperties;
import com.example.mansshop_boot.domain.dto.cache.CacheRequest;
import com.example.mansshop_boot.domain.enumeration.RedisCaching;
import com.example.mansshop_boot.repository.memberQnA.MemberQnARepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderRepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnARepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCacheServiceImpl implements AdminCacheService{

    private final ProductOrderRepository productOrderRepository;

    private final MemberQnARepository memberQnARepository;

    private final ProductQnARepository productQnARepository;

    private final ProductReviewRepository productReviewRepository;

    private final CacheProperties cacheProperties;

    private final RedisTemplate<String, Long> redisTemplate;

    private Map<String, Function<CacheRequest, Long>> KEY_ACTION_MAP;

    @PostConstruct
    void init() {
        KEY_ACTION_MAP = Map.of(
                RedisCaching.ADMIN_PRODUCT_QNA_COUNT.getKey(),
                req -> productQnARepository.findAllByAdminProductQnACount(req.getPageDTO()),
                RedisCaching.ADMIN_MEMBER_QNA_COUNT.getKey(),
                req -> memberQnARepository.findAllByAdminMemberQnACount(req.getPageDTO()),
                RedisCaching.ADMIN_ORDER_COUNT.getKey(),
                req -> productOrderRepository.findAllOrderListCount(req.getPageDTO()),
                RedisCaching.ADMIN_REVIEW_COUNT.getKey(),
                req -> productReviewRepository.countByAdminReviewList(req.getPageDTO(), req.getListType())
        );
    }

    /**
     *
     * @param cachingKey
     * @param request
     * @return
     *
     * Double-check 전략.
     * 많이 사용되는 캐싱이라면 @Scheduled 를 통한 주기적인 동기화를 시행하는 것이 옳겠으나
     * 관리자 기능인만큼 주기적인 갱신은 필요하지 않을 것이라고 생각해 Doudle-check로 처리.
     */
    public long getFullScanCountCache(RedisCaching cachingKey, CacheRequest request) {
        String key = cachingKey.getKey();

        Long result = redisTemplate.opsForValue().get(key);
        if(result == null){
            synchronized (this) {
                result = redisTemplate.opsForValue().get(key);
                if(result == null) {
                    Function<CacheRequest, Long> action = KEY_ACTION_MAP.get(key);

                    if(action == null)
                        throw new IllegalArgumentException("caching Key is Abnormal");

                    result = action.apply(request);
                    long ttl = cacheProperties.getCount().get(key).getTtl();
                    redisTemplate.opsForValue().set(key, result, Duration.ofMinutes(ttl));
                }
            }
        }

        return result;
    }
}
