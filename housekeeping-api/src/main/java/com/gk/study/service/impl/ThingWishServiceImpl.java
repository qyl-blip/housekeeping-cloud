package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.mapper.ThingWishMapper;
import com.gk.study.service.ThingWishService;
import com.gk.study.entity.ThingWish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 心愿单（ThingWish）业务实现。
 *
 * <p>心愿记录用于标记“用户想要/计划购买某个服务（Thing）”。
 * 典型流程：Controller 先判断是否已加入心愿单，再插入记录，并同步更新服务心愿数。</p>
 */
@Service
public class ThingWishServiceImpl extends ServiceImpl<ThingWishMapper, ThingWish> implements ThingWishService {
    @Autowired
    ThingWishMapper mapper;

    /**
     * 查询用户心愿单列表（带服务信息回填）。
     */
    @Override
    public List<Map> getThingWishList(String userId) {
        return mapper.getThingWishList(userId);
    }

    /**
     * 新增心愿记录。
     */
    @Override
    public void createThingWish(ThingWish thingWish) {
        mapper.insert(thingWish);
    }

    /**
     * 删除心愿记录（按心愿ID）。
     */
    @Override
    public void deleteThingWish(String id) {
        mapper.deleteById(id);
    }

    /**
     * 按主键查询心愿记录。
     */
    @Override
    public ThingWish getThingWishById(Long id) {
        return mapper.selectById(id);
    }

    /**
     * 根据 userId + thingId 删除心愿记录（若存在）。
     *
     * @return 被删除的记录（如果不存在则返回 null）
     */
    @Override
    public ThingWish deleteThingWishByUserAndThing(String userId, String thingId) {
        QueryWrapper<ThingWish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("thing_id", thingId)
                .eq("user_id", userId);
        ThingWish record = mapper.selectOne(queryWrapper);
        if (record != null) {
            mapper.deleteById(record.getId());
        }
        return record;
    }

    /**
     * 查询某个用户是否已把某服务加入心愿单。
     */
    @Override
    public ThingWish getThingWish(String userId, String thingId) {
        QueryWrapper<ThingWish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("thing_id", thingId)
                .eq("user_id", userId);
        return mapper.selectOne(queryWrapper);
    }
}









