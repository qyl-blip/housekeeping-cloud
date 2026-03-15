package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.mapper.ThingCollectMapper;
import com.gk.study.service.ThingCollectService;
import com.gk.study.entity.ThingCollect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 收藏（ThingCollect）业务实现。
 *
 * <p>收藏记录用于标记“用户收藏了某个服务（Thing）”。
 * 典型流程：
 * Controller 先做“是否已收藏”的校验，然后插入收藏记录，并同步更新服务收藏数。</p>
 */
@Service
public class ThingCollectServiceImpl extends ServiceImpl<ThingCollectMapper, ThingCollect> implements ThingCollectService {
    @Autowired
    ThingCollectMapper mapper;

    /**
     * 查询用户收藏列表（带服务信息回填）。
     */
    @Override
    public List<Map> getThingCollectList(String userId) {
        return mapper.getThingCollectList(userId);
    }

    /**
     * 新增收藏记录。
     */
    @Override
    public void createThingCollect(ThingCollect thingCollect) {
        mapper.insert(thingCollect);
    }

    /**
     * 删除收藏记录（按收藏ID）。
     */
    @Override
    public void deleteThingCollect(String id) {
        mapper.deleteById(id);
    }

    /**
     * 按主键查询收藏记录。
     */
    @Override
    public ThingCollect getThingCollectById(Long id) {
        return mapper.selectById(id);
    }

    /**
     * 根据 userId + thingId 删除收藏记录（若存在）。
     *
     * @return 被删除的记录（如果不存在则返回 null）
     */
    @Override
    public ThingCollect deleteThingCollectByUserAndThing(String userId, String thingId) {
        QueryWrapper<ThingCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("thing_id", thingId)
                .eq("user_id", userId);
        ThingCollect record = mapper.selectOne(queryWrapper);
        if (record != null) {
            mapper.deleteById(record.getId());
        }
        return record;
    }

    /**
     * 查询某个用户是否收藏过某个服务。
     */
    @Override
    public ThingCollect getThingCollect(String userId, String thingId) {
        QueryWrapper<ThingCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("thing_id", thingId)
                .eq("user_id", userId);
        return mapper.selectOne(queryWrapper);
    }
}









