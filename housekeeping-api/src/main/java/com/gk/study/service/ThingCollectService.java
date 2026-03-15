package com.gk.study.service;

import com.gk.study.entity.ThingCollect;

import java.util.List;
import java.util.Map;

/**
 * 收藏（ThingCollect）业务接口。
 *
 * <p>用户对服务（Thing）的“收藏”行为：新增收藏、取消收藏、查询用户收藏列表等。</p>
 */
public interface ThingCollectService {

    /**
     * 查询某个用户的收藏列表。
     *
     * <p>返回结果通常是 join 后的 Map（包含服务标题/封面等展示字段）。</p>
     */
    List<Map> getThingCollectList(String userId);

    /**
     * 新增收藏记录。
     */
    void createThingCollect(ThingCollect thingCollect);

    /**
     * 删除收藏记录（按收藏ID）。
     */
    void deleteThingCollect(String id);

    /**
     * 按主键查询收藏记录。
     */
    ThingCollect getThingCollectById(Long id);

    /**
     * 根据 userId + thingId 删除收藏记录（若存在）。
     *
     * @return 被删除的记录（如果不存在则返回 null）
     */
    ThingCollect deleteThingCollectByUserAndThing(String userId, String thingId);

    /**
     * 查询某个用户是否收藏过某个服务。
     */
    ThingCollect getThingCollect(String userId, String thingId);
}









