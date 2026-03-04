package com.gk.study.local.service;

import com.gk.study.local.entity.Thing;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 对应论文 4.3：机器学习排序层实现
 */
@Service
public class RecommendationService {

    /**
     * 4.3.2 在线预估服务实现
     * 简单的线性加权模型：Score = 价格权重 + 热度权重 + 距离权重
     */
    public void calculateScore(List<Thing> things, double userLat, double userLng) {
        for (Thing t : things) {
            // 1. 距离因子（距离越近分数越高）
            double distance = 0;
            if (t.getLatitude() != null && t.getLongitude() != null) {
                // 假设 LocationUtils 已经写好了
                // distance = LocationUtils.getDistance(userLat, userLng, t.getLatitude(), t.getLongitude());
            }

            // 2. 热度因子（浏览量越高分数越高）
            double heatScore = t.getVisitCount() * 0.5; // 权重 0.5

            // 3. 价格因子（假设用户喜欢便宜的，价格越低分数越高）
            double priceScore = (1000 - t.getPrice()) * 0.3; // 权重 0.3

            // 综合打分
            double finalScore = heatScore + priceScore;
            t.setScore(finalScore);
        }

        // 按分数降序排序
        things.sort((o1, o2) -> Double.compare(o2.getScore(), o1.getScore()));
    }
}