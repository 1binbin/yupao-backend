package com.xiaobin.yupaobackend.model.dto;

import com.xiaobin.yupaobackend.model.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询队伍信息业务包装类
 *
 * @Author hongxiaobin
 * @Time 2023/3/5-19:02
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TeamQuery extends PageRequest {
    /**
     * id
     */
    private Long id;

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
     * 用户id（队长 id）
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;
}
