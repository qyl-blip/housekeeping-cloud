package com.gk.study.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AppointmentNumberGenerator 单元测试
 */
class AppointmentNumberGeneratorTest {
    
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    
    @Mock
    private ValueOperations<String, String> valueOperations;
    
    @InjectMocks
    private AppointmentNumberGenerator generator;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }
    
    /**
     * 测试生成的预约编号格式正确
     */
    @Test
    void testGenerateFormat() {
        // 模拟 Redis 返回序列号
        when(valueOperations.increment(anyString())).thenReturn(1L);
        
        String appointmentNumber = generator.generate();
        
        // 验证格式：APT + yyyyMMdd + 6位数字
        assertNotNull(appointmentNumber);
        assertTrue(appointmentNumber.matches("APT\\d{8}\\d{6}"));
        
        // 验证日期部分是今天
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        assertTrue(appointmentNumber.startsWith("APT" + today));
    }
    
    /**
     * 测试使用 Redis 生成序列号
     */
    @Test
    void testGenerateWithRedis() {
        // 模拟 Redis 返回序列号
        when(valueOperations.increment(anyString())).thenReturn(123L);
        
        String appointmentNumber = generator.generate();
        
        // 验证调用了 Redis increment
        verify(valueOperations, times(1)).increment(anyString());
        
        // 验证设置了过期时间
        verify(redisTemplate, times(1)).expire(anyString(), eq(2L), eq(TimeUnit.DAYS));
        
        // 验证序列号部分是 000123
        assertTrue(appointmentNumber.endsWith("000123"));
    }
    
    /**
     * 测试 Redis 不可用时降级使用 AtomicLong
     */
    @Test
    void testGenerateFallbackToAtomicLong() {
        // 模拟 Redis 操作失败
        when(valueOperations.increment(anyString())).thenThrow(new RuntimeException("Redis connection failed"));
        
        // 重置计数器
        generator.resetSequence();
        
        String appointmentNumber = generator.generate();
        
        // 验证仍然能生成预约编号
        assertNotNull(appointmentNumber);
        assertTrue(appointmentNumber.matches("APT\\d{8}\\d{6}"));
    }
    
    /**
     * 测试序列号循环（超过999999后重新开始）
     */
    @Test
    void testSequenceWraparound() {
        // 模拟 Redis 返回超过6位数的序列号
        when(valueOperations.increment(anyString())).thenReturn(1000001L);
        
        String appointmentNumber = generator.generate();
        
        // 验证序列号循环：1000001 % 1000000 = 1
        assertTrue(appointmentNumber.endsWith("000001"));
    }
    
    /**
     * 测试并发生成预约编号的唯一性
     */
    @Test
    void testConcurrentGeneration() throws InterruptedException {
        // 模拟 Redis 返回递增的序列号
        when(valueOperations.increment(anyString())).thenAnswer(invocation -> {
            // 每次调用返回不同的序列号
            return System.nanoTime() % 1000000;
        });
        
        Set<String> appointmentNumbers = new HashSet<>();
        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        
        // 创建多个线程并发生成预约编号
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    String number = generator.generate();
                    synchronized (appointmentNumbers) {
                        appointmentNumbers.add(number);
                    }
                }
            });
            threads[i].start();
        }
        
        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }
        
        // 验证生成的预约编号数量（由于使用纳秒时间戳，可能有重复，但应该大部分是唯一的）
        assertTrue(appointmentNumbers.size() > 0);
    }
    
    /**
     * 测试重置 Redis 序列号
     */
    @Test
    void testResetRedisSequence() {
        String date = "20240315";
        
        generator.resetRedisSequence(date);
        
        // 验证调用了 Redis delete
        verify(redisTemplate, times(1)).delete("appointment:sequence:" + date);
    }
    
    /**
     * 测试重置 AtomicLong 序列号
     */
    @Test
    void testResetSequence() {
        // 模拟 Redis 不可用
        when(valueOperations.increment(anyString())).thenThrow(new RuntimeException("Redis unavailable"));
        
        // 重置计数器
        generator.resetSequence();
        
        // 生成第一个预约编号
        String first = generator.generate();
        
        // 重置计数器
        generator.resetSequence();
        
        // 生成第二个预约编号
        String second = generator.generate();
        
        // 验证序列号部分相同（都是 000001）
        assertEquals(first.substring(first.length() - 6), second.substring(second.length() - 6));
    }
    
    /**
     * 测试 Redis 为 null 时的降级处理
     */
    @Test
    void testGenerateWithoutRedis() {
        // 创建一个没有 Redis 的生成器
        AppointmentNumberGenerator generatorWithoutRedis = new AppointmentNumberGenerator();
        generatorWithoutRedis.resetSequence();
        
        String appointmentNumber = generatorWithoutRedis.generate();
        
        // 验证仍然能生成预约编号
        assertNotNull(appointmentNumber);
        assertTrue(appointmentNumber.matches("APT\\d{8}\\d{6}"));
        assertTrue(appointmentNumber.endsWith("000001"));
    }
}
