package com.gk.study.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 预约编号生成器
 * 生成格式: APT + yyyyMMdd + 6位序列号
 * 例如: APT20240315000001
 * 
 * 优先使用 Redis 生成序列号，如果 Redis 不可用则降级使用 AtomicLong
 */
@Component
public class AppointmentNumberGenerator {
    
    private static final String PREFIX = "APT";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String REDIS_KEY_PREFIX = "appointment:sequence:";
    private static final AtomicLong fallbackSequence = new AtomicLong(0);
    
    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;
    
    /**
     * 生成唯一的预约编号
     * @return 预约编号，格式: APT + yyyyMMdd + 6位序列号
     */
    public String generate() {
        String date = LocalDate.now().format(DATE_FORMATTER);
        long seq = getNextSequence(date);
        String sequenceStr = String.format("%06d", seq % 1000000);
        return PREFIX + date + sequenceStr;
    }
    
    /**
     * 获取下一个序列号
     * 优先使用 Redis，如果失败则降级使用 AtomicLong
     * @param date 日期字符串
     * @return 序列号
     */
    private long getNextSequence(String date) {
        if (redisTemplate != null) {
            try {
                String key = REDIS_KEY_PREFIX + date;
                Long seq = redisTemplate.opsForValue().increment(key);
                if (seq != null) {
                    // 设置过期时间为2天，避免 Redis 中积累过多的 key
                    redisTemplate.expire(key, 2, TimeUnit.DAYS);
                    return seq;
                }
            } catch (Exception e) {
                // Redis 操作失败，降级使用 AtomicLong
            }
        }
        // 降级使用 AtomicLong
        return fallbackSequence.incrementAndGet();
    }
    
    /**
     * 重置 Redis 中指定日期的序列号
     * @param date 日期字符串 (yyyyMMdd)
     */
    public void resetRedisSequence(String date) {
        if (redisTemplate != null) {
            String key = REDIS_KEY_PREFIX + date;
            redisTemplate.delete(key);
        }
    }
    
    /**
     * 重置 AtomicLong 序列号（仅用于测试）
     */
    public void resetSequence() {
        fallbackSequence.set(0);
    }
}
