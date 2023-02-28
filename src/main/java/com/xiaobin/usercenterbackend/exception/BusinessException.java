package com.xiaobin.usercenterbackend.exception;

import com.xiaobin.usercenterbackend.common.ErrorCode;

/**
 * 全局异常类
 *
 * @Author hongxiaobin
 * @Time 2023/2/28-16:33
 */
public class BusinessException extends RuntimeException {

    private final int code;
    private final String description;

    /**
     * 全参构造函数
     *
     * @param message     错误信息
     * @param code        错误码
     * @param description 错误信息描述
     */
    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    /**
     * 全参构造函数
     *
     * @param errorCode 错误枚举类
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    /**
     * 全参构造函数
     *
     * @param errorCode   错误枚举类
     * @param description 错误信息描述
     */
    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
