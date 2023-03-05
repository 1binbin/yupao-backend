package com.xiaobin.yupaobackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaobin.yupaobackend.mapper.UserTeamMapper;
import com.xiaobin.yupaobackend.model.domain.UserTeam;
import com.xiaobin.yupaobackend.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
 * @author hongxiaobin
 * @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
 * @createDate 2023-03-05 18:38:21
 */
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
        implements UserTeamService {

}




