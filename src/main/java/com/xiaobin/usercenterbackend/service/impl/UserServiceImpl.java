package com.xiaobin.usercenterbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaobin.usercenterbackend.mapper.UserMapper;
import com.xiaobin.usercenterbackend.model.domain.User;
import com.xiaobin.usercenterbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.regex.Pattern;

import static com.xiaobin.usercenterbackend.contant.UserConstant.SALT;
import static com.xiaobin.usercenterbackend.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 *
 * @author hongxiaobin
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2023-02-26 16:59:19
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return -1;
        }
        if (userAccount.length() < 4) {
            return -1;
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            return -1;
        }
        // 2.校验账户不能包含特殊字符
        String pattern = "^[\\u4E00-\\u9FA5A-Za-z0-9]+$";
        boolean matches = Pattern.matches(pattern, userAccount);
        if (!matches) {
            return -1;
        }
        // 3.校验密码和二次密码是否相同
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }
        // 4.账户不能重复
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(userQueryWrapper);
        if (count > 0) {
            return -1;
        }

        // 5.密码加密（一般使用MD5，这里使用一个工具库）
        String digestPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 6.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(digestPassword);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }
        // 2.校验账户不能包含特殊字符
        String pattern = "^[\\u4E00-\\u9FA5A-Za-z0-9]+$";
        boolean matches = Pattern.matches(pattern, userAccount);
        if (!matches) {
            return null;
        }

        String digestPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        userQueryWrapper.eq("userPassword", digestPassword);

        User user = userMapper.selectOne(userQueryWrapper);
        if (user == null) {
            log.info("User login error, account or password error");
            return null;
        }
        // TODO: 2023/2/26 单位之间内限制错误登录次数

        // 3.记录用户的登录状态
        User safeUser = getSafeUser(user);
        HttpSession session = request.getSession();
        session.setAttribute(USER_LOGIN_STATE, safeUser);
        // 4.登录成功返回脱密信息
        return safeUser;
    }

    /**
     * 用户脱敏
     *
     * @param user 没有脱敏的用户信息
     * @return 已经脱敏的用户信息
     */
    @Override
    public User getSafeUser(User user) {
        // 在Controller层和Service层都必须对用户进行判空
        if (user == null) {
            return null;
        }
        User handlerUser = new User();
        handlerUser.setId(user.getId());
        handlerUser.setUsername(user.getUsername());
        handlerUser.setUserAccount(user.getUserAccount());
        handlerUser.setAvatarUrl(user.getAvatarUrl());
        handlerUser.setGender(user.getGender());
        handlerUser.setPhone(user.getPhone());
        handlerUser.setEmail(user.getEmail());
        handlerUser.setCreateTime(user.getCreateTime());
        handlerUser.setUserStatus(user.getUserStatus());
        handlerUser.setPlanetCode(user.getPlanetCode());
        handlerUser.setUserRole(user.getUserRole());
        return handlerUser;
    }

    @Override
    public int userLoginOut(HttpServletRequest request) {
        if (request == null) {
            return 0;
        }
        // 移除session中保存的用户信息即为注销
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




