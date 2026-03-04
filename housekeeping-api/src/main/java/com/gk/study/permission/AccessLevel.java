package com.gk.study.permission;

public enum AccessLevel {

    //
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








