package com.gk.study.service;


import com.gk.study.entity.Tag;

import java.util.List;

/**
 * 标签业务接口。
 *
 * <p>标签用于对服务（Thing）做细粒度描述，并通过 thing_tag 关联表与服务建立多对多关系。</p>
 */
public interface TagService {

    /**
     * 查询标签列表。
     */
    List<Tag> getTagList();

    /**
     * 新增标签。
     */
    void createTag(Tag tag);

    /**
     * 删除标签。
     */
    void deleteTag(String id);

    /**
     * 更新标签。
     */
    void updateTag(Tag tag);
}









