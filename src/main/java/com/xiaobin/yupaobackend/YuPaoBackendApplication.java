package com.xiaobin.yupaobackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author hongxiaobin
 */

@SpringBootApplication
@MapperScan("com.xiaobin.yupaobackend.mapper")
@EnableScheduling
public class YuPaoBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(YuPaoBackendApplication.class, args);
    }
}
