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
import java.util.stream.Collectors;

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

    @PostMapping("/logout")
    public Integer userLoginOut(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return userService.userLoginOut(request);
    }

    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return new ArrayList<>();
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            // 两边都模糊的查询 即 '%username%'
            userQueryWrapper.like("username", username);
        }
        List<User> list = userService.list(userQueryWrapper);
        // 先转换为数据流，循环设置每个密码为空，再拼接成一个完整的list
        return list.stream().map(user -> userService.getSafeUser(user)).collect(Collectors.toList());
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

    @GetMapping("/current")
    public User getCurrent(HttpServletRequest httpServletRequest) {
        Object userObject = httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObject;
        // 对于存在频繁更新的数据需要去数据库重新获取
        // 对于没有存在频繁更新的数据可以直接在session缓存中读取并返回，提高性能
        if (currentUser == null) {
            return null;
        }
        Long userId = currentUser.getId();
        // TODO: 2023/2/27 检验用户是否合法
        User userServiceById = userService.getById(userId);
        return userService.getSafeUser(userServiceById);
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
