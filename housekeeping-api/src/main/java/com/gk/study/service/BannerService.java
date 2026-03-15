package com.gk.study.service;


import com.gk.study.entity.Banner;

import java.util.List;

/**
 * 轮播图（Banner）业务接口。
 *
 * <p>提供轮播图列表查询与后台增删改。</p>
 */
public interface BannerService {

    /**
     * 查询轮播图列表。
     */
    List<Banner> getBannerList();

    /**
     * 新增轮播图。
     */
    void createBanner(Banner banner);

    /**
     * 删除轮播图。
     */
    void deleteBanner(String id);

    /**
     * 更新轮播图。
     */
    void updateBanner(Banner banner);
}









