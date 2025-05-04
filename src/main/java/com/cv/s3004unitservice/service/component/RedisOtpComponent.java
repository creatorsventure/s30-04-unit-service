package com.cv.s3004unitservice.service.component;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class RedisOtpComponent {

    private final StringRedisTemplate redisTemplate;

    private static final String OTP_SECRET_PREFIX = "otp:secret:";

    /**
     * Save secret with TTL.
     */
    public void saveSecret(String key, String secret, Duration ttl) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(OTP_SECRET_PREFIX + key, Base64.getEncoder().encodeToString(secret.getBytes()), ttl);
    }

    /**
     * Get secret for username.
     */
    public String getSecret(String key) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return new String(Base64.getDecoder().decode(ops.get(OTP_SECRET_PREFIX + key)));
    }

    /**
     * Delete secret manually if needed.
     */
    public void deleteSecret(String key) {
        redisTemplate.delete(OTP_SECRET_PREFIX + key);
    }
}
