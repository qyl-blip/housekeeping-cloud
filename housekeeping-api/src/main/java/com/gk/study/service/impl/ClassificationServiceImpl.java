package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.mapper.ClassificationMapper;
import com.gk.study.entity.Classification;
import com.gk.study.service.ClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类业务实现。
 *
 * <p>分类属于基础字典数据，提供最基本的增删改查能力。</p>
 */
@Service
public class ClassificationServiceImpl extends ServiceImpl<ClassificationMapper, Classification> implements ClassificationService {

    @Autowired
    ClassificationMapper mapper;

    /**
     * 查询分类列表。
     */
    @Override
    public List<Classification> getClassificationList() {
        return mapper.selectList(new QueryWrapper<>());
    }

    /**
     * 新增分类（自动补齐 createTime）。
     */
    @Override
    public void createClassification(Classification classification) {
        classification.setCreateTime(String.valueOf(System.currentTimeMillis()));
        mapper.insert(classification);
    }

    /**
     * 删除分类。
     */
    @Override
    public void deleteClassification(String id) {
        mapper.deleteById(id);
    }

    /**
     * 更新分类。
     */
    @Override
    public void updateClassification(Classification classification) {
        mapper.updateById(classification);
    }
}









