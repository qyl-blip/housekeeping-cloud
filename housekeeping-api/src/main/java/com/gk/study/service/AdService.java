package com.gk.study.service;

import com.gk.study.entity.Ad;

import java.util.List;

/**
 * 广告（Ad）业务接口。
 */
public interface AdService {

    /**
     * 查询广告列表。
     */
    List<Ad> getAdList();

    /**
     * 新增广告。
     */
    void createAd(Ad ad);

    /**
     * 删除广告。
     */
    void deleteAd(String id);

    /**
     * 更新广告。
     */
    void updateAd(Ad ad);
}









