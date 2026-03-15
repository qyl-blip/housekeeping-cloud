package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Notice;
import com.gk.study.mapper.NoticeMapper;
import com.gk.study.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公告业务实现。
 *
 * <p>公告属于运营内容，提供最基本的增删改查能力。</p>
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {
    @Autowired
    NoticeMapper mapper;

    /**
     * 查询公告列表。
     */
    @Override
    public List<Notice> getNoticeList() {
        return mapper.selectList(new QueryWrapper<>());
    }

    /**
     * 新增公告（自动补齐 createTime）。
     */
    @Override
    public void createNotice(Notice notice) {
        notice.setCreateTime(String.valueOf(System.currentTimeMillis()));
        mapper.insert(notice);
    }

    /**
     * 删除公告。
     */
    @Override
    public void deleteNotice(String id) {
        mapper.deleteById(id);
    }

    /**
     * 更新公告。
     */
    @Override
    public void updateNotice(Notice notice) {
        mapper.updateById(notice);
    }
}









