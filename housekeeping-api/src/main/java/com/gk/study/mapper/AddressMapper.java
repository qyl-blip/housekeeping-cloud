package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.Address;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收货地址（地址簿）Mapper。
 *
 * <p>对应表：b_address。该模块主要使用 MyBatis-Plus 的通用 CRUD 能力，
 * 默认地址（def）互斥等业务规则在 Service/Controller 层处理。</p>
 */
@Mapper
public interface AddressMapper extends BaseMapper<Address> {

}









