package com.gk.study.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 预约编号生成器
 * 生成格式: APT + yyyyMMdd + 6位序列号
 * 例如: APT20240315000001
 */
public class AppointmentNumberGenerator {
    
    private static final String PREFIX = "APT";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final AtomicLong sequence = new AtomicLong(0);
    
    /**
     * 生成唯一的预约编号
     * @return 预约编号，格式: APT + yyyyMMdd + 6位序列号
     */
    public static String generate() {
        String date = LocalDate.now().format(DATE_FORMATTER);
        long seq = sequence.incrementAndGet();
        String sequenceStr = String.format("%06d", seq % 1000000);
        return PREFIX + date + sequenceStr;
    }
    
    /**
     * 重置序列号（主要用于测试）
     */
    public static void resetSequence() {
        sequence.set(0);
    }
    
    /**
     * 获取当前序列号（主要用于测试）
     * @return 当前序列号
     */
    public static long getCurrentSequence() {
        return sequence.get();
    }
}
