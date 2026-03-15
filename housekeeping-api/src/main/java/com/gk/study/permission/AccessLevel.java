package com.gk.study.permission;

/**
 * 接口访问权限等级。
 *
 * <p>配合 {@link com.gk.study.permission.Access} 注解使用：
 * - LOGIN：登录用户可访问；
 * - DEMO：演示账号权限（通常限制部分写操作）；
 * - ADMIN：管理员可访问；
 * - SUPER：超级管理员可访问。</p>
 */
public enum AccessLevel {

    LOGIN(1, "all"),
    DEMO(2, "demo"),
    ADMIN(3, "admin"),
    SUPER(4, "super");

    int code;
    String msg;

    AccessLevel(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
