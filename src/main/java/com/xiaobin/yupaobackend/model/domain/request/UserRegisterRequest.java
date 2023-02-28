package com.xiaobin.yupaobackend.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @Author hongxiaobin
 * @Time 2023/2/26-21:16
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = -3747650419376855810L;

    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String planetCode;
}
