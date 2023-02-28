package com.xiaobin.yupaobackend.common;

/**
 * 全局错误码
 *
 * @Author hongxiaobin
 * @Time 2023/2/28-16:08
 */
public enum ErrorCode {

    /**
     * 请求参数为空
     */
    PARAMS_ERROR(40000, "请求参数错误", ""),

    /**
     * 请求数据为空
     */
    NULL_ERROR(40001, "请求数据为空", ""),
    /**
     * 未登录
     */
    NO_LOGIN(40100, "未登录", ""),
    /**
     * 无权限
     */
    NO_AUTH(40101, "无权限", ""),
    /**
     * 系统内部异常
     */
    SYSTEM_ERROR(50000,"系统内部异常","");


    /**
     * 错误码
     */
    private final int code;
    /**
     * 错误信息
     */
    private final String message;
    /**
     * 信息描述
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
