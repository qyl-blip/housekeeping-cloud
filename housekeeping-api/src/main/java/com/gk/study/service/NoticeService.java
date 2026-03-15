package com.gk.study.service;


import com.gk.study.entity.Notice;

import java.util.List;

/**
 * 公告业务接口。
 *
 * <p>提供公告的查询与增删改能力，供前台展示与后台维护。</p>
 */
public interface NoticeService {

    /**
     * 查询公告列表。
     */
    List<Notice> getNoticeList();

    /**
     * 新增公告。
     */
    void createNotice(Notice notice);

    /**
     * 删除公告。
     */
    void deleteNotice(String id);

    /**
     * 更新公告。
     */
    void updateNotice(Notice notice);
}









