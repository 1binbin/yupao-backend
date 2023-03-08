package com.xiaobin.yupaobackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaobin.yupaobackend.common.BaseResponse;
import com.xiaobin.yupaobackend.common.ErrorCode;
import com.xiaobin.yupaobackend.common.ResultUtils;
import com.xiaobin.yupaobackend.exception.BusinessException;
import com.xiaobin.yupaobackend.model.domain.Team;
import com.xiaobin.yupaobackend.model.domain.User;
import com.xiaobin.yupaobackend.model.domain.UserTeam;
import com.xiaobin.yupaobackend.model.dto.TeamQuery;
import com.xiaobin.yupaobackend.model.request.*;
import com.xiaobin.yupaobackend.model.vo.TeamUserVo;
import com.xiaobin.yupaobackend.service.TeamService;
import com.xiaobin.yupaobackend.service.UserService;
import com.xiaobin.yupaobackend.service.UserTeamService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 队伍管理控制器
 *
 * @Author hongxiaobin
 * @Time 2023/3/5-18:44
 */
@Api("队伍管理接口")
@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:3000"})
@Slf4j
public class TeamController {
    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;
    @Resource
    private UserTeamService userTeamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍信息为空");
        }
        Team team = new Team();
        User loginUser = userService.getLoginUser(request);
        BeanUtils.copyProperties(teamAddRequest, team);
        long result = teamService.addTeam(team, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍ID不正确");
        }
        Long teamId = deleteRequest.getId();
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(teamId, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍删除失败");
        }
        return ResultUtils.success(true);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍信息为空");
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍更新失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍ID不正确");
        }
        Team result = teamService.getById(id);
        if (result == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍信息为空");
        }
        return ResultUtils.success(result);
    }

    @GetMapping("/list")
    public BaseResponse<List<TeamUserVo>> getTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        boolean admin = userService.isAdmin(request);
        List<TeamUserVo> list = teamService.listTeams(teamQuery, admin);
        // 查询当前用户是否已加入队伍
        final List<Long> teamIdList = list.stream().map(TeamUserVo::getId).collect(Collectors.toList());
        QueryWrapper<UserTeam> teamQueryWrapper = new QueryWrapper<>();
        try {
            User loginUser = userService.getLoginUser(request);
            teamQueryWrapper.eq("userId", loginUser.getId());
            teamQueryWrapper.in("teamId", teamIdList);
            List<UserTeam> userTeams = userTeamService.list(teamQueryWrapper);
            Set<Long> hasJoinTeamId = userTeams.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            list.forEach(team -> {
                boolean hasJoin = hasJoinTeamId.contains(team.getId());
                team.setHasJoin(hasJoin);
            });
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, e.getMessage());
        }
        // 查询加入队伍的人数
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("teamId", teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        // 队伍ID映射到加入这个队伍的用户列表
        Map<Long, List<UserTeam>> teamIdUserTeamList = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        list.forEach(team -> team.setHasJoinNum(teamIdUserTeamList.getOrDefault(team.getId(), new ArrayList<>()).size()));
        return ResultUtils.success(list);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> getTeamsByPage(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        int pageNum = teamQuery.getPageNum();
        int pageSize = teamQuery.getPageSize();
        Page<Team> page = new Page<>(pageNum, pageSize);
        Page<Team> list = teamService.page(page, queryWrapper);
        return ResultUtils.success(list);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuiteRequest teamQuiteRequest, HttpServletRequest request) {
        if (teamQuiteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍ID为空");
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.quitTeam(teamQuiteRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 获取我创建的队伍
     *
     * @param teamQuery 搜索信息
     * @param request   登录态
     * @return 队伍信息
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVo>> listMyCreateTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getId());
        List<TeamUserVo> list = teamService.listTeams(teamQuery, true);
        return ResultUtils.success(list);
    }

    /**
     * 获取我加入的队伍
     *
     * @param teamQuery 搜索信息
     * @param request   登录态
     * @return 队伍信息
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVo>> listMyJoinTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", loginUser.getId());
        List<UserTeam> teamList = userTeamService.list(userTeamQueryWrapper);
        // 取出不重复的队伍ID
        Map<Long, List<UserTeam>> collect = teamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        ArrayList<Long> longArrayList = new ArrayList<>(collect.keySet());
        teamQuery.setIdList(longArrayList);
        List<TeamUserVo> list = teamService.listTeams(teamQuery, true);
        return ResultUtils.success(list);
    }
}
