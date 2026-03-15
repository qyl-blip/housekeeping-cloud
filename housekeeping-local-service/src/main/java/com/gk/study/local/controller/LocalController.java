package com.gk.study.local.controller;

import com.gk.study.local.entity.Thing;
import com.gk.study.local.service.LocalService;
import com.gk.study.local.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/local")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class LocalController {

    @Autowired
    private LocalService localService;

    @Autowired
    private RecommendationService recommendationService;

    /**
     * 升级版接口：支持分类筛选
     * classificationId 说明：
     * - 不传：展示全部（附近家政）
     * - 传 2：金牌月嫂（根据你数据库分类表 ID 确定）
     * - 传 4：保洁特惠
     */
    @GetMapping("/list")
    public List<Thing> list(@RequestParam String city,
                            @RequestParam(required = false) Long classificationId,
                            @RequestParam(required = false) Double lat,
                            @RequestParam(required = false) Double lng,
                            @RequestParam(required = false) Long userId) {

        System.out.println(">>> [DEBUG] 接收到请求：city=" + city + ", classificationId=" + classificationId);

        // 1. 获取基础数据：现在支持传入分类 ID 进行过滤
        List<Thing> services = localService.getServicesByCityAndType(city, classificationId);

        System.out.println(">>> [DEBUG] LocalService 返回 " + (services != null ? services.size() : 0) + " 条数据");

        // 2. A/B 测试与机器学习排序逻辑保持不变
        if (userId != null && userId % 2 == 0) {
            System.out.println(">>> 命中实验组 A：启用机器学习排序模型");
            if (lat != null && lng != null) {
                recommendationService.calculateScore(services, lat, lng);
            }
        } else {
            System.out.println(">>> 命中对照组 B：使用默认排序");
        }

        return services;
    }

    /**
     * 调试接口：直接查询数据库中的所有数据
     */
    @GetMapping("/debug")
    public List<Thing> debug() {
        return localService.getAllServices();
    }

    /**
     * 清除缓存接口
     */
    @GetMapping("/clearCache")
    public String clearCache(@RequestParam(required = false) String city) {
        return localService.clearCache(city);
    }
}