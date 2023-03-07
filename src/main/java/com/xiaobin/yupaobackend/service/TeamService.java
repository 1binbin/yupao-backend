package com.xiaobin.yupaobackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaobin.yupaobackend.model.domain.Team;
import com.xiaobin.yupaobackend.model.domain.User;
import com.xiaobin.yupaobackend.model.dto.TeamQuery;
import com.xiaobin.yupaobackend.model.request.TeamJoinRequest;
import com.xiaobin.yupaobackend.model.request.TeamQuiteRequest;
import com.xiaobin.yupaobackend.model.request.TeamUpdateRequest;
import com.xiaobin.yupaobackend.model.vo.TeamUserVo;

import java.util.List;

/**
 * @author hongxiaobin
 * @description 针对表【team(队伍)】的数据库操作Service
 * @createDate 2023-03-05 18:38:21
 */
public interface TeamService extends IService<Team> {
    /**
     * 创建队伍
     *
     * @param team      队伍信息
     * @param loginUser 当前登录用户
     * @return 是否插入成功
     */
    long addTeam(Team team, User loginUser);

    /**
     * 搜索队伍
     *
     * @param teamQuery 搜索信息
     * @param isAdmin   是否为管理员
     * @return 搜索结果
     */
    List<TeamUserVo> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更改队伍信息
     *
     * @param teamUpdateRequest 新的队伍信息
     * @param loginUser         当前登录用户
     * @return 是否创建成功
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 加入队伍
     *
     * @param teamJoinRequest 加入队伍信息
     * @return 是否加入成功
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 提出队伍
     *
     * @param teamQuiteRequest 队伍ID
     * @param loginUser        登录用户信息
     * @return 是否退出成功
     */
    boolean quitTeam(TeamQuiteRequest teamQuiteRequest, User loginUser);

    /**
     * 队长解散队伍
     *
     * @param teamId 队伍ID
     * @param loginUser 登录用户信息
     * @return 是否解散成功
     */
    boolean deleteTeam(Long teamId, User loginUser);
}
