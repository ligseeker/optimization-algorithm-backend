package com.example.optimization_algorithm_backend.common.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisSafeClient {

    private static final Logger log = LoggerFactory.getLogger(RedisSafeClient.class);

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisSafeClient(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception ex) {
            log.warn("Redis读取失败, key={}, error={}", key, ex.getMessage());
            return null;
        }
    }

    public void set(String key, Object value, long ttl, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl, timeUnit);
        } catch (Exception ex) {
            log.warn("Redis写入失败, key={}, error={}", key, ex.getMessage());
        }
    }

    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception ex) {
            log.warn("Redis写入失败, key={}, error={}", key, ex.getMessage());
        }
    }

    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception ex) {
            log.warn("Redis删除失败, key={}, error={}", key, ex.getMessage());
        }
    }
}
