package com.gk.study.service;


import com.gk.study.entity.Thing;

import java.util.List;

public interface ThingService {

    /**
     * 前台/后台的服务列表查询。
     *
     * @param keyword 关键词（匹配服务标题）
     * @param sort 排序方式：recent(最新)、hot(最热)、recommend(按热度)
     * @param c 分类ID（-1 表示不过滤）
     * @param tag 标签ID（为空表示不过滤）
     * @return 满足条件的服务列表（每条服务会携带 tags 字段）
     */
    List<Thing> getThingList(String keyword, String sort, String c, String tag);

    /**
     * 创建服务（通常用于后台/管理员创建，或通用创建入口）。
     *
     * @param thing 服务实体（部分字段会在实现层补齐默认值，如 createTime、pv 等）
     */
    void createThing(Thing thing);

    /**
     * 根据ID删除服务。
     *
     * @param id 服务ID
     */
    void deleteThing(String id);

    /**
     * 更新服务信息（包含服务标签关联更新）。
     *
     * @param thing 服务实体（需包含 id）
     */
    void updateThing(Thing thing);

    /**
     * 获取服务详情。
     *
     * 注意：当前实现会在查询详情时自增 pv（浏览量）。
     *
     * @param id 服务ID
     * @return 服务详情
     */
    Thing getThingById(String id);

    /**
     * 心愿单数量 +1。
     *
     * @param thingId 服务ID
     */
    void addWishCount(String thingId);

    /**
     * 收藏数量 +1。
     *
     * @param thingId 服务ID
     */
    void addCollectCount(String thingId);

    /**
     * 心愿单数量 -1（最低为 0）。
     *
     * @param thingId 服务ID
     */
    void subWishCount(String thingId);

    /**
     * 收藏数量 -1（最低为 0）。
     *
     * @param thingId 服务ID
     */
    void subCollectCount(String thingId);

    /**
     * 查询某个用户发布的服务列表。
     *
     * @param userId 用户ID
     * @return 服务列表
     */
    List<Thing> getUserThing(String userId);

    /**
     * 浏览量 +1（不返回对象，仅更新）。
     *
     * @param id 服务ID
     */
    void incrementPv(String id);
}









