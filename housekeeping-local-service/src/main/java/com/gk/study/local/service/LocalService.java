package com.gk.study.local.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gk.study.local.entity.Thing;
import com.gk.study.local.feign.UserClient;
import com.gk.study.local.mapper.ThingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
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
        // 1. 定义缓存 Key，比如 "local:service:北京"
        String cacheKey = "local:service:v2:" + city;

        // 2. 先查 Redis 缓存
        if (redisTemplate.hasKey(cacheKey)) {
            System.out.println(">>> 命中缓存，直接返回 Redis 数据");
            return (List<Thing>) redisTemplate.opsForValue().get(cacheKey);
        }

        // 3. 缓存没有，查数据库
        System.out.println(">>> 缓存未命中，查询数据库...");
        QueryWrapper<Thing> query = new QueryWrapper<>();
        query.eq("city", city); // WHERE city = '北京'
        List<Thing> list = thingMapper.selectList(query);

        // 4. 写入 Redis，设置 10 分钟过期（模拟毕设的高性能优化）
        if (list != null && !list.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, list, 10, TimeUnit.MINUTES);
        }

        // 👇 模拟微服务调用 (对应论文 5.1)
        try {
            System.out.println(">>> 正在通过 Feign 调用旧服务(housekeeping-api)...");
            String result = userClient.getClassificationList();
            System.out.println(">>> Feign 调用成功！旧服务返回数据长度：" + (result != null ? result.length() : 0));
        } catch (Exception e) {
            System.err.println(">>> Feign 调用失败 (不影响主流程): " + e.getMessage());
        }


        return list;
    }
    public List<Thing> getServicesByCityAndType(String city, Long classificationId) {
        // 1. 缓存 Key 也要加上分类 ID，防止数据混淆
        String cacheKey = "local:service:v2:" + city + ":" + (classificationId == null ? "all" : classificationId);

        if (redisTemplate.hasKey(cacheKey)) {
            return (List<Thing>) redisTemplate.opsForValue().get(cacheKey);
        }

        // 2. 数据库查询增加分类条件
        QueryWrapper<Thing> query = new QueryWrapper<>();
        query.eq("city", city);

        // 如果传了分类ID，就加一个 WHERE classification_id = xxx
        if (classificationId != null) {
            query.eq("classification_id", classificationId);
        }

        List<Thing> list = thingMapper.selectList(query);
        if (list != null && !list.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, list, 10, TimeUnit.MINUTES);
        }
        return list;
    }
}
