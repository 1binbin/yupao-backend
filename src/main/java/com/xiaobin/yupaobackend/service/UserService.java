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
     * @param tagList 标签列表
     * @return
     */
    List<User> searchUserByTags(List<String> tagList);
}
