package com.xiaobin.usercenterbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaobin.usercenterbackend.model.domain.User;

/**
 * 用户服务
 *
 * @author hongxiaobin
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2023-02-26 16:59:19
 */
public interface UserService extends IService<User> {

    /**
     * 实现用户注册逻辑
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return 用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);
}
