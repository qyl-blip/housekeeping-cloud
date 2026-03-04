package com.gk.study.local.utils;

/**
 * 对应论文 4.1：地理位置定位模块实现
 * 4.1.2 定位结果校验与城市匹配
 */
public class LocationUtils {

    // 模拟：计算两个经纬度之间的距离（单位：米）
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        // 这里省略复杂的地球弧度计算，毕设演示可以用简单的勾股定理模拟距离
        // 真实项目请搜索 "Java Haversine algorithm"
        double x = lat1 - lat2;
        double y = lng1 - lng2;
        return Math.sqrt(x * x + y * y) * 111000; // 粗略估算
    }

    // 模拟：根据经纬度判断城市（论文 4.1.2）
    public static String resolveCity(double lat, double lng) {
        // 简单模拟，真实情况需要调用高德地图API
        if (lat > 39 && lat < 41) return "北京";
        if (lat > 30 && lat < 32) return "上海";
        return "未知城市";
    }
}