package com.xiaobin.yupaobackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author hongxiaobin
 */

@SpringBootApplication
@MapperScan("com.xiaobin.usercenterbackend.mapper")
public class UserCenterBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserCenterBackendApplication.class, args);
    }
}
