package com.gk.study.exception;

/**
 * 无效日期异常
 * 当用户尝试预约过去的日期或无效日期时抛出此异常
 */
public class InvalidDateException extends RuntimeException {
    
    /**
     * 构造函数 - 仅包含错误消息
     * @param message 错误消息
     */
    public InvalidDateException(String message) {
        super(message);
    }
    
    /**
     * 构造函数 - 包含错误消息和原因
     * @param message 错误消息
     * @param cause 异常原因
     */
    public InvalidDateException(String message, Throwable cause) {
        super(message, cause);
    }
}
