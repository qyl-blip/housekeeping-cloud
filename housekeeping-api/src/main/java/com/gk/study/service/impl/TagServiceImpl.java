package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.mapper.TagMapper;
import com.gk.study.entity.Tag;
import com.gk.study.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 标签业务实现。
 *
 * <p>标签属于基础字典数据，提供最基本的增删改查能力。</p>
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    @Autowired
    TagMapper mapper;

    /**
     * 查询标签列表。
     */
    @Override
    public List<Tag> getTagList() {
        return mapper.selectList(new QueryWrapper<>());
    }

    /**
     * 新增标签（自动补齐 createTime）。
     */
    @Override
    public void createTag(Tag tag) {
        tag.setCreateTime(String.valueOf(System.currentTimeMillis()));
        mapper.insert(tag);
    }

    /**
     * 删除标签。
     */
    @Override
    public void deleteTag(String id) {
        mapper.deleteById(id);
    }

    /**
     * 更新标签。
     */
    @Override
    public void updateTag(Tag tag) {
        mapper.updateById(tag);
    }
}









