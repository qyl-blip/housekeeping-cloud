package com.gk.study.local.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gk.study.local.entity.Thing;
import com.gk.study.local.feign.UserClient;
import com.gk.study.local.mapper.ThingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class LocalService {

    @Autowired
    private ThingMapper thingMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserClient userClient;

    public List<Thing> getServicesByCity(String city) {
        // 直接查数据库，不使用缓存
        System.out.println(">>> 直接查询数据库，city=" + city);
        
        QueryWrapper<Thing> query = new QueryWrapper<>();
        query.eq("city", city);
        List<Thing> list = thingMapper.selectList(query);
        
        System.out.println(">>> 查询结果：" + (list != null ? list.size() : 0) + " 条数据");
        
        return list;
    }
    
    public List<Thing> getServicesByCityAndType(String city, Long classificationId) {
        // 直接查数据库，不使用缓存
        System.out.println(">>> 直接查询数据库，city=" + city + ", classificationId=" + classificationId);
        
        QueryWrapper<Thing> query = new QueryWrapper<>();
        query.eq("city", city);

        if (classificationId != null) {
            query.eq("classification_id", classificationId);
        }

        List<Thing> list = thingMapper.selectList(query);
        System.out.println(">>> 查询结果：" + (list != null ? list.size() : 0) + " 条数据");
        
        if (list != null && !list.isEmpty()) {
            for (Thing thing : list) {
                System.out.println(">>> 服务详情：id=" + thing.getId() + 
                    ", title=" + thing.getTitle() + 
                    ", city=" + thing.getCity() + 
                    ", classificationId=" + thing.getClassificationId());
            }
        }
        
        return list;
    }

    /**
     * 调试方法：获取所有服务数据
     */
    public List<Thing> getAllServices() {
        System.out.println(">>> [DEBUG] 查询所有服务数据");
        List<Thing> list = thingMapper.selectList(null);
        System.out.println(">>> [DEBUG] 数据库中总共有 " + (list != null ? list.size() : 0) + " 条数据");
        
        if (list != null && !list.isEmpty()) {
            for (Thing thing : list) {
                System.out.println(">>> [DEBUG] 服务：id=" + thing.getId() + 
                    ", title=" + thing.getTitle() + 
                    ", city=" + thing.getCity() + 
                    ", location=" + thing.getLocation() + 
                    ", classificationId=" + thing.getClassificationId());
            }
        }
        
        return list;
    }

    /**
     * 清除缓存方法
     */
    public String clearCache(String city) {
        if (city != null) {
            // 清除指定城市的缓存
            String pattern = "local:service:v2:" + city + "*";
            System.out.println(">>> [DEBUG] 清除缓存模式：" + pattern);
            
            // 获取所有匹配的key
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                System.out.println(">>> [DEBUG] 已清除 " + keys.size() + " 个缓存key");
                return "已清除 " + city + " 相关的 " + keys.size() + " 个缓存";
            } else {
                return "未找到 " + city + " 相关的缓存";
            }
        } else {
            // 清除所有本地服务缓存
            Set<String> keys = redisTemplate.keys("local:service:v2:*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                System.out.println(">>> [DEBUG] 已清除所有本地服务缓存，共 " + keys.size() + " 个");
                return "已清除所有本地服务缓存，共 " + keys.size() + " 个";
            } else {
                return "未找到任何本地服务缓存";
            }
        }
    }
}