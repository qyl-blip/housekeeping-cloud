package com.gk.study.exception;

/**
 * 预约冲突异常
 * 当时间段已被完全预约时抛出此异常
 */
public class AppointmentConflictException extends RuntimeException {
    
    /**
     * 构造函数 - 仅包含错误消息
     * @param message 错误消息
     */
    public AppointmentConflictException(String message) {
        super(message);
    }
    
    /**
     * 构造函数 - 包含错误消息和原因
     * @param message 错误消息
     * @param cause 异常原因
     */
    public AppointmentConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
