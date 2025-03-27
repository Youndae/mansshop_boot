package com.example.mansshop_boot.domain.dto.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "cache")
public class CacheProperties {

    private Map<String, Count> count;

    public Map<String, Count> getCount() {
        return count;
    }

    public void setCount(Map<String, Count> count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "CacheProperties{" +
                "count=" + count +
                '}';
    }

    public static class Count {
        private long ttl;

        public long getTtl() {
            return ttl;
        }

        public void setTtl(long ttl) {
            this.ttl = ttl;
        }

        @Override
        public String toString() {
            return "Count{" +
                    "ttl=" + ttl +
                    '}';
        }
    }
}
