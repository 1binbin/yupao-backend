package com.xiaobin.yupaobackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaobin.yupaobackend.common.BaseResponse;
import com.xiaobin.yupaobackend.common.ErrorCode;
import com.xiaobin.yupaobackend.common.ResultUtils;
import com.xiaobin.yupaobackend.exception.BusinessException;
import com.xiaobin.yupaobackend.model.domain.User;
import com.xiaobin.yupaobackend.model.domain.request.UserLoginRequest;
import com.xiaobin.yupaobackend.model.domain.request.UserRegisterRequest;
import com.xiaobin.yupaobackend.service.UserService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.xiaobin.yupaobackend.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务控制层
 *
 * @Author hongxiaobin
 * @Time 2023/2/26-21:07
 */
@Api("用户管理接口")
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:3000"})
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 注册接口
     *
     * @param userRegisterRequest 用户注册信息
     * @return 返回体
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求数据为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        // 进行简单的校验
        if (StringUtils.isAnyBlank(userPassword, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "参数为空");
        }
        long userRegister = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(userRegister);
    }

    /**
     * 登录接口
     *
     * @param userLoginRequest 用户登录信息
     * @param request          用户登录态
     * @return 返回体
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest,
                                        HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请求数据为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "参数为空");
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    /**
     * 退出登录接口
     *
     * @param request 用户登录态
     * @return 返回体
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLoginOut(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN, "账号未登录");
        }
        int result = userService.userLoginOut(request);
        return ResultUtils.success(result);
    }

    /**
     * 根据用户名查询用户信息接口
     *
     * @param username 用户昵称
     * @param request  登录态
     * @return 返回体
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(@RequestParam(required = false) String username, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "账号不是管理员，暂无权限");
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            // 两边都模糊的查询 即 '%username%'
            userQueryWrapper.like("username", username);
        }
        List<User> list = userService.list(userQueryWrapper);
        // 先转换为数据流，循环设置每个密码为空，再拼接成一个完整的list
        List<User> result = list.stream().map(user -> userService.getSafeUser(user)).collect(Collectors.toList());
        return ResultUtils.success(result);
    }

    /**
     * 删除用户接口
     *
     * @param id      要删除的用户id
     * @param request 登录态
     * @return 返回体
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (id < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号小于0");
        }
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "账号不是管理员，暂无权限");
        }
        // 实现的是逻辑删除
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 查询登录用户信息接口
     *
     * @param httpServletRequest 用户登录态
     * @return 返回体
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrent(HttpServletRequest httpServletRequest) {
        Object userObject = httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObject;
        // 对于存在频繁更新的数据需要去数据库重新获取
        // 对于没有存在频繁更新的数据可以直接在session缓存中读取并返回，提高性能
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN, "账号未登录");
        }
        Long userId = currentUser.getId();
        // TODO: 2023/2/27 检验用户是否合法
        User userServiceById = userService.getById(userId);
        User result = userService.getSafeUser(userServiceById);
        return ResultUtils.success(result);
    }

    /**
     * 根据标签查询用户信息接口
     *
     * @param tagNameList 标签列表
     * @return 返回体
     */
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam(required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签列表为空");
        }
        List<User> searchUserByTags = userService.searchUserByTags(tagNameList);
        return ResultUtils.success(searchUserByTags);
    }

    /**
     * 更新用户信息接口
     *
     * @param user    新的用户信息
     * @param request 用户登录态
     * @return 返回体
     */
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        // 1.判空
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户信息为空");
        }
        if (user.getUsername() == null && user.getGender() == null && user.getAvatarUrl() == null && user.getPhone() == null && user.getEmail() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户没有更新信息");
        }
        User loginUser = userService.getLoginUser(request);
        // 2.判断权限
        Integer result = userService.updateUser(user, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 主页推荐用户接口
     * @param pageSize 每一页包含多少数据
     * @param pageNum 当前第几页
     * @return 返回体
     */
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageSize, long pageNum) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        // 分页查询
        Page<User> result = userService.page(new Page<>(pageNum, pageSize), userQueryWrapper);
        return ResultUtils.success(result);
    }
}
