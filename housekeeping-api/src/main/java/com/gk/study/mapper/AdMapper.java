package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.Ad;
import org.apache.ibatis.annotations.Mapper;

/**
 * 广告/运营位（Ad）数据访问层。
 *
 * <p>对应表：b_ad。图片文件的落盘与 image 字段写入由 Controller 负责，Mapper 仅负责 CRUD。</p>
 */
@Mapper
public interface AdMapper extends BaseMapper<Ad> {

}









