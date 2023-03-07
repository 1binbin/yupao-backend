package com.xiaobin.yupaobackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaobin.yupaobackend.common.ErrorCode;
import com.xiaobin.yupaobackend.exception.BusinessException;
import com.xiaobin.yupaobackend.mapper.TeamMapper;
import com.xiaobin.yupaobackend.model.domain.Team;
import com.xiaobin.yupaobackend.model.domain.User;
import com.xiaobin.yupaobackend.model.domain.UserTeam;
import com.xiaobin.yupaobackend.model.dto.TeamQuery;
import com.xiaobin.yupaobackend.model.enums.TeamStatusEnum;
import com.xiaobin.yupaobackend.model.request.TeamJoinRequest;
import com.xiaobin.yupaobackend.model.request.TeamQuiteRequest;
import com.xiaobin.yupaobackend.model.request.TeamUpdateRequest;
import com.xiaobin.yupaobackend.model.vo.TeamUserVo;
import com.xiaobin.yupaobackend.model.vo.UserVo;
import com.xiaobin.yupaobackend.service.TeamService;
import com.xiaobin.yupaobackend.service.UserService;
import com.xiaobin.yupaobackend.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.xiaobin.yupaobackend.contant.UserConstant.SALT;
import static com.xiaobin.yupaobackend.contant.UserConstant.TEAM_SALT;

/**
 * @author hongxiaobin
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2023-03-05 18:38:21
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {
    @Resource
    private TeamService teamService;
    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍信息为空");
        }
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_AUTH, "用户未登录");
        }
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        }
        String teamName = team.getName();
        if (StringUtils.isBlank(teamName) || teamName.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍名称不符合要求");
        }
        String description = team.getDescription();
        if (StringUtils.isBlank(description) || description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getTeamStatusByValue(status);
        if (teamStatusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }
        String password = team.getPassword();
        String digestPassword = "";
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum)) {
            if (StringUtils.isBlank(password) || password.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码格式不正确");
            }
            digestPassword = DigestUtils.md5DigestAsHex((TEAM_SALT + password).getBytes());
        }
        Date date = team.getExpireTime();
        if (new Date().after(date)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间小于当前时间");
        }
        // TODO: 2023/3/5 用户可能同时创建100个队伍，使用加锁解决
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        final long userId = loginUser.getId();
        teamQueryWrapper.eq("userId", userId);
        long count = this.count(teamQueryWrapper);
        if (count >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建5个队伍");
        }
        team.setId(null);
        team.setUserId(userId);
        team.setPassword(digestPassword);
        boolean result = this.save(team);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"添加队伍失败");
        }
        Long teamId = team.getId();
        if (teamId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍创建失败");
        }
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍插入失败");
        }
        return teamId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<TeamUserVo> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            List<Long> idList = teamQuery.getIdList();
            if (CollectionUtils.isNotEmpty(idList)) {
                queryWrapper.in("id",idList);
            }
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }
            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxNum", maxNum);
            }
            Long userId = teamQuery.getUserId();
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }
            Integer status = teamQuery.getStatus();
            TeamStatusEnum teamStatusByValue = TeamStatusEnum.getTeamStatusByValue(status);
            if (teamStatusByValue == null) {
                teamStatusByValue = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && !teamStatusByValue.equals(TeamStatusEnum.PUBLIC)) {
                throw new BusinessException(ErrorCode.NO_AUTH, "非管理员无法查看非公开信息");
            }
            queryWrapper.eq("status", teamStatusByValue.getValue());
        }
        // 不展示已过期队伍
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        List<Team> list = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        ArrayList<TeamUserVo> teamUserVos = new ArrayList<>();
        // 关联查询创建人的用户信息
        for (Team team : list) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVo teamUserVo = new TeamUserVo();
            BeanUtils.copyProperties(team, teamUserVo);
            // 用户信息脱敏
            if (user != null) {
                UserVo userVo = new UserVo();
                BeanUtils.copyProperties(user, userVo);
                teamUserVo.setCreateUser(userVo);
            }
            teamUserVos.add(teamUserVo);
        }
        return teamUserVos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍新的信息为空");
        }
        Long id = teamUpdateRequest.getId();
        Team oldTeam = getTeamInfo(id);
        if (!oldTeam.getUserId().equals(loginUser.getId()) && userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限更改");
        }
        String digestPassword = "";
        TeamStatusEnum teamStatusByValue = TeamStatusEnum.getTeamStatusByValue(teamUpdateRequest.getStatus());
        if (teamStatusByValue.equals(TeamStatusEnum.SECRET)) {
            String password = teamUpdateRequest.getPassword();
            if (StringUtils.isNotBlank(password)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密队伍需要密码");
            }
            digestPassword = DigestUtils.md5DigestAsHex((TEAM_SALT + password).getBytes());
        }
        Team team = new Team();
        teamUpdateRequest.setPassword(digestPassword);
        BeanUtils.copyProperties(teamUpdateRequest, team);
        return this.updateById(team);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍信息为空");
        }
        Long teamId = teamJoinRequest.getTeamId();
        Team team = getTeamInfo(teamId);
        Date expireTime = team.getExpireTime();
        if (expireTime != null && new Date().before(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getTeamStatusByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止加入私有的队伍");
        }
        String password = teamJoinRequest.getPassword();
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum)) {
            if (StringUtils.isBlank(password)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码为空");
            }
            String digestPassword = DigestUtils.md5DigestAsHex((TEAM_SALT + password).getBytes());
            if (!digestPassword.equals(team.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不正确");
            }
        }
        // 已加入的队伍的数量
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", userId);
        long count = userTeamService.count(userTeamQueryWrapper);
        if (count > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已加入的队伍大于5");
        }
        // 只能加入未满的队伍
        long hashCount = this.getTeamCountUserById(teamId);
        if (hashCount >= team.getMaxNum()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满");
        }
        // 不能重复加入已加入的队伍
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", userId);
        userTeamQueryWrapper.eq("teamId", teamId);
        long hashJoinCount = userTeamService.count(userTeamQueryWrapper);
        if (hashJoinCount > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已加入的队伍大于5");
        }
        // 新增队伍中的用户
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuiteRequest teamQuiteRequest, User loginUser) {
        if (teamQuiteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍ID对象为空");
        }
        Long teamId = teamQuiteRequest.getTeamId();
        Team team = getTeamInfo(teamId);
        // 校验是否已加入队伍
        Long userId = loginUser.getId();
        UserTeam userTeam = new UserTeam();
        userTeam.setId(teamId);
        userTeam.setUserId(userId);
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>(userTeam);
        long count = userTeamService.count(userTeamQueryWrapper);
        if (count == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入队伍");
        }
        long countUserById = this.getTeamCountUserById(teamId);
        if (countUserById == 1) {
            // 删除队伍和所有加入的用户的关系
            this.removeById(teamId);
        } else {
            // 是否为队长
            if (userId.equals(team.getUserId())) {
                // 把队伍转移给最早加入的用户——也就是ID最小的
                QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("teamId", teamId).orderByAsc("id");
                List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "获取用户信息错误");
                }
                UserTeam nextUser = userTeamList.get(1);
                // 更新队伍队长ID
                Long nextUserUserId = nextUser.getUserId();
                Team updateTeam = new Team();
                updateTeam.setUserId(nextUserUserId);
                // TODO: 2023/3/7  这里的ID是不是有问题
                updateTeam.setId(teamId);
                boolean result = this.updateById(updateTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新队伍失败");
                }
            }
        }
        return userTeamService.remove(userTeamQueryWrapper);
    }

    @Override
    /* 删除用户信息和删除队伍信息只能同时成功 */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(Long teamId, User loginUser) {
        Team teamInfo = getTeamInfo(teamId);
        Long teamInfoId = teamInfo.getId();
        if (!teamInfo.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限");
        }
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamInfoId);
        // 删除队伍的所有用户
        boolean remove = userTeamService.remove(userTeamQueryWrapper);
        if (!remove) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除用户信息错误");
        }
        // 删除队伍
        return this.removeById(teamInfoId);
    }

    /**
     * 获取某队伍加入的人数
     *
     * @param teamID 队伍ID
     * @return 已加入的人数
     */
    private long getTeamCountUserById(long teamID) {
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamID);
        return userTeamService.count(userTeamQueryWrapper);
    }

    /**
     * 根据队伍ID获取队伍信息
     *
     * @param teamId 队伍ID
     * @return 队伍信息
     */
    private Team getTeamInfo(Long teamId) {
        if (teamId == null || teamId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍ID不正确");
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍信息为空");
        }
        return team;
    }
}




