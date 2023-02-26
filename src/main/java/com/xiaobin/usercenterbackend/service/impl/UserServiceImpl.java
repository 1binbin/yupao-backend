package com.xiaobin.usercenterbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaobin.usercenterbackend.model.domain.User;
import com.xiaobin.usercenterbackend.service.UserService;
import com.xiaobin.usercenterbackend.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author hongxiaobin
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2023-02-26 16:59:19
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




