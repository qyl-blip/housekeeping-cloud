package com.gk.study.interceptor;

import com.google.gson.Gson;
import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.OpLog;
import com.gk.study.entity.User;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.OpLogService;
import com.gk.study.service.UserService;
import com.gk.study.utils.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class AccessInterceptor extends HandlerInterceptorAdapter {

    private final Logger logger = LoggerFactory.getLogger(AccessInterceptor.class);

    private static OpLogService service;
    private static UserService userService;

    @Autowired
    public void setOpLogService(OpLogService service) {
        AccessInterceptor.service = service;
    }

    @Autowired
    public void setUserService(UserService userService) {
        AccessInterceptor.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 跳过OPTIONS预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        request.setAttribute("_startTime", System.currentTimeMillis());

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Access access = method.getAnnotation(Access.class);
        if (access == null) {
            return true;
        }

        if (access.level() == AccessLevel.LOGIN
                || access.level() == AccessLevel.DEMO
                || access.level() == AccessLevel.ADMIN
                || access.level() == AccessLevel.SUPER) {
            String token = request.getHeader("TOKEN");
            if (token == null || token.trim().isEmpty()) {
                writeResponse(response, new APIResponse(ResponeCode.UNAUTHORIZED, "未登录"));
                return false;
            }
            User user = userService.getUserByToken(token);
            if (user == null) {
                writeResponse(response, new APIResponse(ResponeCode.UNAUTHORIZED, "未登录"));
                return false;
            }

            // role: 1=普通用户, 2=演示账号, 3=管理员
            // 但实际登录逻辑中 role > 1 都算管理员
            int userRole = Integer.parseInt(user.getRole());
            
            if (access.level() == AccessLevel.DEMO && userRole < 2) {
                writeResponse(response, new APIResponse(ResponeCode.UNAUTHORIZED, "权限不足"));
                return false;
            }
            if (access.level() == AccessLevel.ADMIN && userRole < 2) {
                writeResponse(response, new APIResponse(ResponeCode.UNAUTHORIZED, "权限不足"));
                return false;
            }
            if (access.level() == AccessLevel.SUPER && userRole < 3) {
                writeResponse(response, new APIResponse(ResponeCode.UNAUTHORIZED, "权限不足"));
                return false;
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // no-op
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (service == null) {
            return;
        }
        Long endTime = System.currentTimeMillis();
        Long startTime = (Long) request.getAttribute("_startTime");
        Long diff = (startTime == null) ? 0L : (endTime - startTime);

        OpLog opLog = new OpLog();
        opLog.setReIp(IpUtils.getIpAddr(request));
        opLog.setReMethod(request.getMethod());
        opLog.setReUrl(request.getRequestURI());
        opLog.setReUa(request.getHeader(HttpHeaders.USER_AGENT));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        opLog.setReTime(formatter.format(new Date()));
        opLog.setAccessTime(String.valueOf(diff));
        service.createOpLog(opLog);
    }

    private void writeResponse(HttpServletResponse response, APIResponse apiResponse) throws IOException {
        response.setStatus(200);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        Gson gson = new Gson();
        response.getWriter().println(gson.toJson(apiResponse));
        response.getWriter().flush();
    }
}