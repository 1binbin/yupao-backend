package com.xiaobin.usercenterbackend.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author hongxiaobin
 * @Time 2023/2/26-21:36
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 6062988635817605597L;

    private String userAccount;
    private String userPassword;
}
