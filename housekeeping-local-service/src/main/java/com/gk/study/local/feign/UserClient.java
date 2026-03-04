package com.gk.study.local.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// 对应论文 5.1.2 跨服务接口联调
// "housekeeping-api" 是你在 Nacos 里看到的旧服务名
@FeignClient(name = "housekeeping-api")
public interface UserClient {

    // 调用旧系统的接口获取用户信息
    @GetMapping("/api/user/info")
    Object getUserInfo(@RequestParam("id") Long id);

    @GetMapping("/api/classification/list")
    String getClassificationList();
}