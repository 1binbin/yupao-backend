package com.xiaobin.usercenterbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiaobin.usercenterbackend.model.domain.User;
import com.xiaobin.usercenterbackend.model.domain.request.UserLoginRequest;
import com.xiaobin.usercenterbackend.model.domain.request.UserRegisterRequest;
import com.xiaobin.usercenterbackend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.xiaobin.usercenterbackend.contant.UserConstant.ADMIN_ROLE;
import static com.xiaobin.usercenterbackend.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务控制层
 *
 * @Author hongxiaobin
 * @Time 2023/2/26-21:07
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        // 进行简单的校验
        if (StringUtils.isAnyBlank(userPassword, userPassword, checkPassword)) {
            return null;
        }
        return userService.userRegister(userAccount, userPassword, checkPassword);
    }

    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest,
                          HttpServletRequest request) {
        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        return userService.userLogin(userAccount, userPassword, request);
    }

    @GetMapping("/search")
    public List<User> searchUsers(@RequestBody String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return new ArrayList<>();
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            // 两边都模糊的查询 即 '%username%'
            userQueryWrapper.like("username", username);
        }
        return userService.list(userQueryWrapper);
    }

    @PostMapping("/delete")
    public boolean deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (id < 0) {
            return false;
        }
        if (!isAdmin(request)) {
            return false;
        }
        // 实现的是逻辑删除
        return userService.removeById(id);
    }

    /**
     * 鉴权 仅管理员可查询
     *
     * @param request 请求域
     * @return 是否为管理员
     */
    private boolean isAdmin(HttpServletRequest request) {
        Object userObject = request.getSession().getAttribute(USER_LOGIN_STATE);
        // 强转不需要判空，如果为空转为空
        User user = (User) userObject;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
}
