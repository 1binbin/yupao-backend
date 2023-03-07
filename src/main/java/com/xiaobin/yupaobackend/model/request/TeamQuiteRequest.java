package com.xiaobin.yupaobackend.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 请求退出队伍实体类
 * @Author hongxiaobin
 * @Time 2023/3/6-16:00
 */
@Data
public class TeamQuiteRequest implements Serializable {
    private static final long serialVersionUID = -6759395627914971767L;
    /**
     * 队伍ID
     */
    private Long teamId;
}
