package com.example.eshighlevelapi.controller.base;

/**
 * 请求响应code码枚举
 *
 * @author liuxiaokun
 * @version 1.0.0
 * @since 2022年8月4日
 */
public enum CodeEnum {

    /**
     * 请求成功
     */
    SUCCESS(20000, "请求成功"),

    /**
     * 缺少必填参数
     */
    LACK_PARAM(40000, "缺少必要参数"),

    /**
     * 未授权,没有访问权限
     */
    FORBIDDEN(40003, "未授权"),

    /**
     * 服务器错误的宽泛类型，具体其他细节继续定义
     * 主要用于统一异常处理中。
     */
    SERVER_INTERNAL_ERROR(50000, "服务器错误");


    private final int code;

    private final String desc;

    CodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
