package com.smtp.ratelimit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimiterTest {

    @Mock StringRedisTemplate redis;
    @Mock ValueOperations<String, String> valueOps;
    @InjectMocks RateLimiter limiter;

    @Test
    void firstRequest_isAllowed() {
        when(redis.opsForValue()).thenReturn(valueOps);
        when(valueOps.increment("ratelimit:sender1.com")).thenReturn(1L);
        assertThat(limiter.isAllowed("sender1.com")).isTrue();
    }

    @Test
    void requestAtLimit_isAllowed() {
        when(redis.opsForValue()).thenReturn(valueOps);
        when(valueOps.increment("ratelimit:at-limit.com")).thenReturn(1000L);
        assertThat(limiter.isAllowed("at-limit.com")).isTrue();
    }

    @Test
    void requestOverLimit_isDenied() {
        when(redis.opsForValue()).thenReturn(valueOps);
        when(valueOps.increment("ratelimit:burst-domain.com")).thenReturn(1001L);
        assertThat(limiter.isAllowed("burst-domain.com")).isFalse();
    }
}
