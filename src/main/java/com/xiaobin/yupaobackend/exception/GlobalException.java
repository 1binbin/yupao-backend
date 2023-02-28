package com.xiaobin.yupaobackend.exception;

import com.xiaobin.yupaobackend.common.BaseResponse;
import com.xiaobin.yupaobackend.common.ErrorCode;
import com.xiaobin.yupaobackend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @Author hongxiaobin
 * @Time 2023/2/28-16:51
 */
@RestControllerAdvice
@Slf4j
public class GlobalException {
    /**
     * 捕获 BusinessException异常
     *
     * @param e 需要捕获的异常
     * @return void
     */
    @ExceptionHandler(BusinessException.class)
    public <T> BaseResponse<T> businessExceptionHandler(BusinessException e) {
        log.error("businessException" + e.getDescription(), e);
        return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    /**
     * 捕获系统内部异常
     *
     * @param e 需要捕获的异常
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public <T> BaseResponse<T> runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage(), "");
    }
}
