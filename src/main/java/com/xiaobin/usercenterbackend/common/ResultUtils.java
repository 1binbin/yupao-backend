package com.xiaobin.usercenterbackend.common;

/**
 * 生成通用返回对象
 *
 * @Author hongxiaobin
 * @Time 2023/2/28-15:54
 */
public class ResultUtils {
    /**
     * 成功时返回对象
     *
     * @param data 返回体数据
     * @return void
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }


    /**
     * 错误时返回异常
     *
     * @param errorCode 异常码
     * @return void
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }


    public static <T> BaseResponse<T> error(ErrorCode errorCode, String message, String description) {
        return new BaseResponse<>(errorCode, message, description);
    }

    public static <T> BaseResponse<T> error(int code, String message, String description) {
        return new BaseResponse<>(code, message, description);
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode, String description) {
        return new BaseResponse<>(errorCode, description);
    }
}
