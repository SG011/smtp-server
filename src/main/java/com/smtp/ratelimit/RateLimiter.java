package com.smtp.ratelimit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
public class RateLimiter {
    private static final int LIMIT = 1000;
    private static final Duration WINDOW = Duration.ofMinutes(1);
    private final StringRedisTemplate redis;

    public RateLimiter(StringRedisTemplate redis) { this.redis = redis; }

    public boolean isAllowed(String senderDomain) {
        String key = "ratelimit:" + senderDomain;
        Long count = redis.opsForValue().increment(key);
        if (count == 1) redis.expire(key, WINDOW);
        return count != null && count <= LIMIT;
    }
}
