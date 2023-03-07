package com.xiaobin.yupaobackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import static com.xiaobin.yupaobackend.contant.UserConstant.TEAM_SALT;

@SpringBootTest
class YuPaoBackendApplicationTests {

    @Test
    void contextLoads() {
        String password = "";
        String s = DigestUtils.md5DigestAsHex((TEAM_SALT + password).getBytes());
        System.out.println(s);
    }

}
