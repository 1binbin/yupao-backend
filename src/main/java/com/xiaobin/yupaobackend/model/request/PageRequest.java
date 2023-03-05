package com.xiaobin.yupaobackend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询请求类
 *
 * @Author hongxiaobin
 * @Time 2023/3/5-19:19
 */
@Data
public class PageRequest implements Serializable {
    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -6759395627914971767L;

    /**
     * 页面大小
     */
    private int pageSize = 10;
    /**
     * 当前第几页
     */
    private int pageNum = 1;
}
