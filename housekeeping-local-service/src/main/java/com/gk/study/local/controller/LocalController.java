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
                            @RequestParam(required = false) Long classificationId, // 👈 新增：分类ID
                            @RequestParam(required = false) Double lat,
                            @RequestParam(required = false) Double lng,
                            @RequestParam(required = false) Long userId) {

        // 1. 获取基础数据：现在支持传入分类 ID 进行过滤
        // 注意：你需要同步去 LocalService 里修改这个方法（见下方）
        List<Thing> services = localService.getServicesByCityAndType(city, classificationId);

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
}