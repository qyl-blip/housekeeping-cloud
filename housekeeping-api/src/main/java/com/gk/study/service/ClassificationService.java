package com.gk.study.service;


import com.gk.study.entity.Classification;

import java.util.List;

/**
 * 分类业务接口。
 *
 * <p>分类用于对服务（Thing）做归类展示，便于前端筛选与运营维护。</p>
 */
public interface ClassificationService {

    /**
     * 查询分类列表。
     */
    List<Classification> getClassificationList();

    /**
     * 新增分类。
     */
    void createClassification(Classification classification);

    /**
     * 删除分类。
     */
    void deleteClassification(String id);

    /**
     * 更新分类。
     */
    void updateClassification(Classification classification);
}









