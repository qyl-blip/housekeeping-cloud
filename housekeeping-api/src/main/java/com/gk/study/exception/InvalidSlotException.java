package com.gk.study.exception;

/**
 * 无效时间段异常
 * 当时间段ID无效或时间段已被禁用时抛出此异常
 */
public class InvalidSlotException extends RuntimeException {
    
    /**
     * 构造函数 - 仅包含错误消息
     * @param message 错误消息
     */
    public InvalidSlotException(String message) {
        super(message);
    }
    
    /**
     * 构造函数 - 包含错误消息和原因
     * @param message 错误消息
     * @param cause 异常原因
     */
    public InvalidSlotException(String message, Throwable cause) {
        super(message, cause);
    }
}
