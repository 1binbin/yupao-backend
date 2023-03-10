package com.xiaobin.yupaobackend.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 请求队伍信息实体类
 *
 * @Author hongxiaobin
 * @Time 2023/3/6-16:00
 */
@Data
public class TeamAddRequest implements Serializable {
    private static final long serialVersionUID = -6759395627914971767L;
    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id（队长 id）
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;
}
