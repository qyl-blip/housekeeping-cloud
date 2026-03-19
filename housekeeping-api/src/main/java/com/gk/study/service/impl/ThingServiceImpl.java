package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.RecommendConfig;
import com.gk.study.entity.Thing;
import com.gk.study.entity.ThingTag;
import com.gk.study.mapper.ThingMapper;
import com.gk.study.mapper.ThingTagMapper;
import com.gk.study.service.RecommendConfigService;
import com.gk.study.service.ThingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 服务（家政项目）业务实现。
 *
 * <p>核心点：</p>
 * <ul>
 *   <li>列表查询使用 {@link QueryWrapper} 组合关键词、排序、分类等条件。</li>
 *   <li>标签筛选通过 thing_tag 中间表进行二次过滤（当前实现为“先查服务，再按标签过滤”）。</li>
 *   <li>详情查询会自增 pv（浏览量），属于“读写混合”的查询。</li>
 * </ul>
 */
@Service
public class ThingServiceImpl extends ServiceImpl<ThingMapper, Thing> implements ThingService {

    @Autowired
    ThingMapper mapper;

    @Autowired
    ThingTagMapper thingTagMapper;

    @Lazy
    @Autowired
    RecommendConfigService recommendConfigService;

    /**
     * 服务列表查询。
     *
     * <p>支持：</p>
     * <ul>
     *   <li>keyword：按 title 模糊匹配</li>
     *   <li>sort：recent(按创建时间)、hot/recommend(按 pv)</li>
     *   <li>c：分类ID（-1 表示不过滤）</li>
     *   <li>tag：标签ID（通过中间表过滤，并给每条服务回填 tags）</li>
     * </ul>
     */
    @Override
    public List<Thing> getThingList(String keyword, String sort, String c, String tag) {
        QueryWrapper<Thing> queryWrapper = new QueryWrapper<>();

        // 1) 关键词过滤（title like）
        queryWrapper.like(StringUtils.isNotBlank(keyword), "title", keyword);

        // 2) 排序：recent/hot 在 SQL 层排序，其余（含 recommend 和默认）在内存中按权重综合分排序
        if ("recent".equals(sort)) {
            queryWrapper.orderBy(true, false, "create_time");
        } else if ("hot".equals(sort)) {
            queryWrapper.orderBy(true, false, "pv");
        }
        // 其他情况不加 SQL 排序，后面统一走权重内存排序

        // 3) 分类过滤
        if (StringUtils.isNotBlank(c) && !c.equals("-1")) {
            queryWrapper.eq(true, "classification_id", c);
        }

        // 4) 先查出基础列表
        List<Thing> things = mapper.selectList(queryWrapper);

        // 5) 标签过滤：通过 thing_tag 关联表做二次筛选
        if (StringUtils.isNotBlank(tag)) {
            List<Thing> filtered = new ArrayList<>();
            QueryWrapper<ThingTag> thingTagQueryWrapper = new QueryWrapper<>();
            thingTagQueryWrapper.eq("tag_id", tag);
            List<ThingTag> thingTagList = thingTagMapper.selectList(thingTagQueryWrapper);

            for (Thing thing : things) {
                for (ThingTag thingTag : thingTagList) {
                    if (thing.getId().equals(thingTag.getThingId())) {
                        filtered.add(thing);
                    }
                }
            }
            things.clear();
            things.addAll(filtered);
        }

        // 6) 给每条服务回填 tags（用于前端展示）
        for (Thing thing : things) {
            QueryWrapper<ThingTag> thingTagQueryWrapper = new QueryWrapper<>();
            thingTagQueryWrapper.lambda().eq(ThingTag::getThingId, thing.getId());
            List<ThingTag> thingTags = thingTagMapper.selectList(thingTagQueryWrapper);
            List<Long> tags = thingTags.stream().map(ThingTag::getTagId).collect(Collectors.toList());
            thing.setTags(tags);
        }

        // 7) 非 recent/hot 模式（含 recommend 和默认无 sort）：按权重综合分排序
        if (!"recent".equals(sort) && !"hot".equals(sort)) {
            RecommendConfig config = recommendConfigService.getActiveConfig();
            double pvW = config.getPvWeight() != null ? config.getPvWeight() : 0.4;
            double wishW = config.getWishWeight() != null ? config.getWishWeight() : 0.2;
            double collectW = config.getCollectWeight() != null ? config.getCollectWeight() : 0.3;
            double scoreW = config.getScoreWeight() != null ? config.getScoreWeight() : 0.1;

            things.sort(Comparator.comparingDouble((Thing t) -> {
                double pv = t.getPv() != null ? Double.parseDouble(t.getPv()) : 0;
                double wish = t.getWishCount() != null ? Double.parseDouble(t.getWishCount()) : 0;
                double collect = t.getCollectCount() != null ? Double.parseDouble(t.getCollectCount()) : 0;
                double score = t.getScore() != null ? Double.parseDouble(t.getScore()) : 0;
                return pv * pvW + wish * wishW + collect * collectW + score * scoreW;
            }).reversed());
        }

        return things;
    }

    /**
     * 创建服务。
     *
     * <p>会自动设置：</p>
     * <ul>
     *   <li>createTime（当前时间戳）</li>
     *   <li>pv/score/wishCount 的默认值（如为空则置 0）</li>
     * </ul>
     */
    @Override
    public void createThing(Thing thing) {
        thing.setCreateTime(String.valueOf(System.currentTimeMillis()));

        if (thing.getPv() == null) {
            thing.setPv("0");
        }
        if (thing.getScore() == null) {
            thing.setScore("0");
        }
        if (thing.getWishCount() == null) {
            thing.setWishCount("0");
        }

        mapper.insert(thing);

        // 绑定标签关联（thing_tag）
        setThingTags(thing);
    }

    /**
     * 删除服务。
     */
    @Override
    public void deleteThing(String id) {
        mapper.deleteById(id);
    }

    /**
     * 更新服务信息（同时更新服务-标签关联）。
     */
    @Override
    public void updateThing(Thing thing) {
        setThingTags(thing);
        mapper.updateById(thing);
    }

    /**
     * 获取服务详情。
     *
     * <p>注意：当前实现会自增 pv（浏览量）并写回数据库。</p>
     */
    @Override
    public Thing getThingById(String id) {
        Thing thing = mapper.selectById(id);
        thing.setPv(String.valueOf(Integer.parseInt(thing.getPv()) + 1));
        mapper.updateById(thing);

        return thing;
    }

    /**
     * 心愿单数量 +1。
     */
    @Override
    public void addWishCount(String thingId) {
        Thing thing = mapper.selectById(thingId);
        thing.setWishCount(String.valueOf(Integer.parseInt(thing.getWishCount()) + 1));
        mapper.updateById(thing);
    }

    /**
     * 收藏数量 +1。
     */
    @Override
    public void addCollectCount(String thingId) {
        Thing thing = mapper.selectById(thingId);
        thing.setCollectCount(String.valueOf(Integer.parseInt(thing.getCollectCount()) + 1));
        mapper.updateById(thing);
    }

    /**
     * 心愿单数量 -1（最低为 0）。
     */
    @Override
    public void subWishCount(String thingId) {
        Thing thing = mapper.selectById(thingId);
        int current = Integer.parseInt(thing.getWishCount());
        thing.setWishCount(String.valueOf(Math.max(0, current - 1)));
        mapper.updateById(thing);
    }

    /**
     * 收藏数量 -1（最低为 0）。
     */
    @Override
    public void subCollectCount(String thingId) {
        Thing thing = mapper.selectById(thingId);
        int current = Integer.parseInt(thing.getCollectCount());
        thing.setCollectCount(String.valueOf(Math.max(0, current - 1)));
        mapper.updateById(thing);
    }

    /**
     * 查询某个用户发布的服务列表。
     */
    @Override
    public List<Thing> getUserThing(String userId) {
        QueryWrapper<Thing> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return mapper.selectList(queryWrapper);
    }

    /**
     * 浏览量 +1（不返回对象，仅更新）。
     */
    @Override
    public void incrementPv(String id) {
        Thing thing = mapper.selectById(id);
        if (thing != null) {
            int currentPv = thing.getPv() != null ? Integer.parseInt(thing.getPv()) : 0;
            thing.setPv(String.valueOf(currentPv + 1));
            mapper.updateById(thing);
        }
    }

    /**
     * 维护服务与标签的关联关系（thing_tag）。
     *
     * <p>策略：先删掉该 thing 的全部关联，再按最新 tags 重新插入。</p>
     */
    public void setThingTags(Thing thing) {
        // 1) 删除旧关联
        Map<String, Object> map = new HashMap<>();
        map.put("thing_id", thing.getId());
        thingTagMapper.deleteByMap(map);

        // 2) 插入新关联
        if (thing.getTags() != null) {
            for (Long tag : thing.getTags()) {
                ThingTag thingTag = new ThingTag();
                thingTag.setThingId(thing.getId());
                thingTag.setTagId(tag);
                thingTagMapper.insert(thingTag);
            }
        }
    }

}
