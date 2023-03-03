package com.xiaobin.yupaobackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaobin.yupaobackend.model.domain.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param planetCode    用户编号
     * @return 用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);


    /**
     * 实现用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request      请求域
     * @return 脱密的用户数据
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param user 没有脱敏的用户信息
     * @return 已经脱敏的用户信息
     */
    User getSafeUser(User user);

    /**
     * 用户注销
     *
     * @param request 请求域
     * @return
     */
    int userLoginOut(HttpServletRequest request);

    /**
     * 根据标签搜索用户
     *
     * @param tagList 标签列表
     * @return
     */
    List<User> searchUserByTags(List<String> tagList);

    /**
     * 更新用户信息
     *
     * @param user      新的用户信息
     * @param loginUser 登录用户信息
     * @return 1-成功，0-失败
     */
    Integer updateUser(User user, User loginUser);

    /**
     * 获取当前登录用户信息
     *
     * @param request 请求域
     * @return 用户信息
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 判断是不是管理员
     *
     * @param request 请求域
     * @return true-管理员
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 判断是不是管理员
     *
     * @param loginUser 登录用户信息
     * @return true-管理员
     */
    boolean isAdmin(User loginUser);
}
