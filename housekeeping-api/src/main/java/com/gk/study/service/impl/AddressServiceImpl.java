package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Address;
import com.gk.study.mapper.AddressMapper;
import com.gk.study.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 地址簿业务实现。
 *
 * <p>基于 MyBatis-Plus 的 {@link ServiceImpl}，通过 {@link AddressMapper} 操作 address 表。</p>
 *
 * <p>说明：默认地址（def）的“互斥”逻辑在 Controller 层处理：当设置 def=1 时，会把同用户其它地址置 0。</p>
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {
    @Autowired
    AddressMapper mapper;

    /**
     * 查询某个用户的地址列表（按 create_time 倒序）。
     */
    @Override
    public List<Address> getAddressList(String userId) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderBy(true, false, "create_time");
        return mapper.selectList(queryWrapper);
    }

    /**
     * 新增地址（自动补齐 createTime）。
     */
    @Override
    public void createAddress(Address address) {
        address.setCreateTime(String.valueOf(System.currentTimeMillis()));
        mapper.insert(address);
    }

    /**
     * 删除地址。
     */
    @Override
    public void deleteAddress(String id) {
        mapper.deleteById(id);
    }

    /**
     * 更新地址。
     */
    @Override
    public void updateAddress(Address address) {
        mapper.updateById(address);
    }
}









