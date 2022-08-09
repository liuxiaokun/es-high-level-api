package com.example.eshighlevelapi.controller.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * restful接口返回统一封装对象
 *
 * @author liuxiaokun
 * @version 1.0.0
 * @since 2022年8月4日
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RO {

    /**
     * 请求是否成功
     */
    private boolean success;

    /**
     * 业务响应码
     *
     * @see CodeEnum
     */
    private int code;

    /**
     * 接口返回提示信息，用于前端展示给用户
     */
    private String msg;

    /**
     * 接口返回的业务数据
     */
    private Object data;

    public RO(boolean success, CodeEnum codeEnum) {
        this.success = success;
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getDesc();
    }

    public RO(boolean success, CodeEnum codeEnum, Object data) {
        this.success = success;
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getDesc();
        this.data = data;
    }

    public RO(boolean success, int code, String msg) {
        this.success = success;
        this.code = code;
        this.msg = msg;
    }

    public static RO success() {
        return new RO(true, CodeEnum.SUCCESS);
    }

    public static RO success(String msg) {
        return new RO(true, CodeEnum.SUCCESS.getCode(), msg);
    }

    public static RO success(Object data) {
        return new RO(true, CodeEnum.SUCCESS, data);
    }

    public static RO fail(String msg) {
        return new RO(false, CodeEnum.SERVER_INTERNAL_ERROR.getCode(), msg);
    }

    public static RO fail(int code, String msg) {
        return new RO(false, code, msg);
    }

    public static RO response(boolean success, CodeEnum codeEnum) {
        return new RO(success, codeEnum);
    }

}
