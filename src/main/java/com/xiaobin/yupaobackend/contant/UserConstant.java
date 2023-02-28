package com.xiaobin.yupaobackend.contant;

/**
 * 用户常量类
 *
 * @Author hongxiaobin
 * @Time 2023/2/26-22:37
 */
public interface UserConstant {
    /**
     * 用户登录态
     */
    String USER_LOGIN_STATE = "userLoginState";

    /**
     * 盐值，混淆密码
     */
    String SALT = "bin";

    /**
     * 用户角色
     */
    int DEFAULT_ROLE = 0;

    /**
     * 管理员角色
     */
    int ADMIN_ROLE = 1;

    /**
     * session超时时间
     */
    int SESSION_TIME = 2 * 24 * 60 * 60;
}
