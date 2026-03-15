package com.gk.study.service;


import com.gk.study.entity.Address;

import java.util.List;

/**
 * 地址簿业务接口。
 *
 * <p>为 {@code /address} Controller 提供数据查询与增删改能力。</p>
 */
public interface AddressService {

    /**
     * 查询某个用户的地址列表。
     *
     * @param userId 用户ID
     */
    List<Address> getAddressList(String userId);

    /**
     * 新增地址。
     */
    void createAddress(Address address);

    /**
     * 删除地址。
     */
    void deleteAddress(String id);

    /**
     * 更新地址。
     */
    void updateAddress(Address address);
}









