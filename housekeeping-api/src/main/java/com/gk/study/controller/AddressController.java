package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.PageUtil;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Address;
import com.gk.study.service.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * 收货地址（地址簿）模块接口。
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>查询某个用户的地址列表</li>
 *   <li>新增/删除/更新地址</li>
 *   <li>维护默认地址：当 def="1" 时，会把该用户其它地址的 def 统一置为 "0"</li>
 * </ul>
 *
 * <p>说明：当前 Controller 未加 {@code @Access} 鉴权注解，默认依赖网关/拦截器或前端约束。
 * 如需更严格控制，可在地址相关接口上补充登录权限。</p>
 */
@RestController
@RequestMapping("/address")
public class AddressController {

    private final static Logger logger = LoggerFactory.getLogger(AddressController.class);

    @Autowired
    AddressService service;

    /**
     * 查询地址列表。
     *
     * @param userId   用户ID（查询该用户的地址簿）
     * @param page     页码（可选）
     * @param pageSize 每页条数（可选）
     * @return 地址列表（可选分页）
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String userId, Integer page, Integer pageSize){
        List<Address> list =  service.getAddressList(userId);
        if (page != null && pageSize != null) {
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", PageUtil.of(list, page, pageSize));
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    /**
     * 新增地址。
     *
     * <p>规则：当 {@code def="1"} 时表示设置为默认地址；此时会把该用户其它地址的默认标记统一清空，
     * 以保证同一用户只有一个默认地址。</p>
     *
     * @param address 前端提交的地址信息
     * @return 操作结果
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Address address) throws IOException {
        // 如果该地址被设置为默认地址（def="1"），先把该用户其它地址的 def 都置为 "0"
        if(address.getDef().equals("1")){
            List<Address> list =  service.getAddressList(address.getUserId());
            for(Address address1: list) {
                address1.setDef("0");
                service.updateAddress(address1);
            }
        }
        service.createAddress(address);
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }

    /**
     * 删除地址（支持批量）。
     *
     * @param ids 地址ID，英文逗号分隔
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteAddress(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }

    /**
     * 更新地址。
     *
     * <p>规则与新增一致：当 {@code def="1"} 时表示设置为默认地址，会先清空该用户其它地址的默认标记。</p>
     *
     * @param address 待更新的地址信息
     * @return 操作结果
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Address address) throws IOException {
        if(address.getDef().equals("1")){
            List<Address> list =  service.getAddressList(address.getUserId());
            for(Address address1: list) {
                address1.setDef("0");
                service.updateAddress(address1);
            }
        }
        service.updateAddress(address);
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }
}





