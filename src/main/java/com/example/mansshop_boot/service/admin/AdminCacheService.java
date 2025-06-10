package com.example.mansshop_boot.service.admin;

import com.example.mansshop_boot.domain.dto.cache.CacheRequest;
import com.example.mansshop_boot.domain.enumeration.RedisCaching;

public interface AdminCacheService {

    long getFullScanCountCache(RedisCaching cachingKey, CacheRequest request);
}
