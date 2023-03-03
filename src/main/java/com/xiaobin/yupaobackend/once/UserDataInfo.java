package com.xiaobin.yupaobackend.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 读取Excel为对象，读取成员编号和成员昵称
 *
 * @Author: hongxiaobin
 * @Time: 2023/3/3 14:53
 */
@Data
public class UserDataInfo {
    /**
     * 用户编号
     */
    @ExcelProperty("成员编号")
    private String planetCode;

    /**
     * 成员昵称
     */
    @ExcelProperty("成员昵称")
    private String username;
}