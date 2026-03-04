package com.gk.study.handler;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.ErrorLog;
import com.gk.study.exception.AppointmentConflictException;
import com.gk.study.exception.InvalidDateException;
import com.gk.study.exception.InvalidSlotException;
import com.gk.study.service.ErrorLogService;
import com.gk.study.utils.HttpContextUtils;
import com.gk.study.utils.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @Autowired
    private ErrorLogService service;

    /**
     * 处理预约冲突异常
     * 当时间段已被完全预约时返回409状态码
     */
    @ExceptionHandler(AppointmentConflictException.class)
    public APIResponse handleAppointmentConflict(AppointmentConflictException ex) {
        logger.warn("预约冲突: {}", ex.getMessage());
        APIResponse response = new APIResponse(ResponeCode.FAIL, ex.getMessage());
        response.setCode(409);
        return response;
    }

    /**
     * 处理无效日期异常
     * 当预约日期无效时返回400状态码
     */
    @ExceptionHandler(InvalidDateException.class)
    public APIResponse handleInvalidDate(InvalidDateException ex) {
        logger.warn("无效日期: {}", ex.getMessage());
        return new APIResponse(ResponeCode.FAIL, ex.getMessage());
    }

    /**
     * 处理无效时间段异常
     * 当时间段ID无效时返回400状态码
     */
    @ExceptionHandler(InvalidSlotException.class)
    public APIResponse handleInvalidSlot(InvalidSlotException ex) {
        logger.warn("无效时间段: {}", ex.getMessage());
        return new APIResponse(ResponeCode.FAIL, ex.getMessage());
    }

    /**
     * 处理通用异常
     * 记录详细日志并返回通用错误消息
     */
    @ExceptionHandler(Exception.class)
    public APIResponse handleException(Exception ex){
        logger.error("error log======>" + ex.getMessage(), ex);

        saveLog(ex);
        return new APIResponse(ResponeCode.FAIL, "系统繁忙，请稍后重试");
    }

    /**
     *
     */
    private void saveLog(Exception ex){
        ErrorLog log = new ErrorLog();
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        log.setIp(IpUtils.getIpAddr(request));
        log.setUrl(request.getRequestURI());
        log.setMethod(request.getMethod());
        Map<String, String> params = HttpContextUtils.getParameterMap(request);
        if(!params.isEmpty()){
            // log.setRequestParams(JsonUtils.toJsonString(params));
        }

        log.setContent(Arrays.toString(ex.getStackTrace()));
        log.setLogTime(String.valueOf(System.currentTimeMillis()));

        service.createErrorLog(log);
    }
}







