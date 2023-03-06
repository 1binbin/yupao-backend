package com.xiaobin.yupaobackend.service;

import com.xiaobin.yupaobackend.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaobin.yupaobackend.model.domain.User;
import com.xiaobin.yupaobackend.model.request.TeamAddRequest;

/**
* @author hongxiaobin
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-03-05 18:38:21
*/
public interface TeamService extends IService<Team> {
    /**
     * 创建队伍
     * @param team 队伍信息
     * @param loginUser 当前登录用户
     * @return 是否插入成功
     */
    long addTeam(Team team, User loginUser);

}
