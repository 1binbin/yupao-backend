package com.xiaobin.yupaobackend.service.impl;

import com.xiaobin.yupaobackend.model.domain.User;
import com.xiaobin.yupaobackend.service.UserService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author hongxiaobin
 * @Time 2023/2/26-18:43
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceImplTest {
    @Resource
    private UserService userService;

    @Test
    void test() {
        String userAccount = "xiaobin++";
        String userPassword = "12345678";
        String checkPassword = "12345678";
        String planetCode = "123456";
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        System.out.println(result);
        // Assertions.assertEquals(3, result);
    }

    @Test
    void testLogin() {
        String userAccount = "xiaobin";
        String userPassword = "12345678";
        // userService.userLogin(userAccount,userPasswordword);
    }

    @Test
    void searchUserByTags() {
        ArrayList<String> list = new ArrayList<>();
        list.add("java");
        list.add("python");
        List<User> users = userService.searchUserByTags(list);
        Assert.assertNotNull(users);
    }
}