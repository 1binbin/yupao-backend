package com.xiaobin.usercenterbackend.service;

import com.xiaobin.usercenterbackend.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @Author hongxiaobin
 * @Time 2023/2/26-17:21
 */
@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;

    @Test
    public void test() {
        User user = new User();
        user.setUsername("test");
        user.setUserAccount("123");
        user.setAvatarUrl("E:\\Project\\usercenter\\user-center-backend");
        user.setGender(0);
        user.setUserPassword("123");
        user.setPhone("15875195553");
        user.setEmail("2812181610@qq.com");
        user.setUserStatus(0);
        user.setUserRole(0);
        user.setPlanetCode("");
        boolean b = userService.save(user);
        System.out.println(b);
        System.out.println(user.getId());
        // 断言 判断实际结果和预期结果是否一样  也就是我觉得   在单元测试中使用
        Assertions.assertTrue(b);
    }

}