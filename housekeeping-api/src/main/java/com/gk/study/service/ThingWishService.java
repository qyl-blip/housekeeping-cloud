package com.gk.study.service;

import com.gk.study.entity.ThingWish;

import java.util.List;
import java.util.Map;

/**
 * 心愿单（ThingWish）业务接口。
 *
 * <p>用户把服务（Thing）加入心愿单/取消心愿，以及查询心愿单列表。</p>
 */
public interface ThingWishService {

    /**
     * 查询某个用户的心愿单列表。
     *
     * <p>返回结果通常是 join 后的 Map（包含服务标题/封面等展示字段）。</p>
     */
    List<Map> getThingWishList(String userId);

    /**
     * 新增心愿记录。
     */
    void createThingWish(ThingWish thingWish);

    /**
     * 删除心愿记录（按心愿ID）。
     */
    void deleteThingWish(String id);

    /**
     * 按主键查询心愿记录。
     */
    ThingWish getThingWishById(Long id);

    /**
     * 根据 userId + thingId 删除心愿记录（若存在）。
     *
     * @return 被删除的记录（如果不存在则返回 null）
     */
    ThingWish deleteThingWishByUserAndThing(String userId, String thingId);

    /**
     * 查询某个用户是否已把某服务加入心愿单。
     */
    ThingWish getThingWish(String userId, String thingId);
}









