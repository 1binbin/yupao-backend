package com.xiaobin.yupaobackend.once;

import com.xiaobin.yupaobackend.mapper.UserMapper;
import com.xiaobin.yupaobackend.model.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

/**
 * 批量插入用户数据到数据库
 *
 * @Author: hongxiaobin
 * @Time: 2023/3/3 15:19
 */
@Component
public class InsertUsers {

    @Resource
    private UserMapper userMapper;

    /**
     * 批量插入用户
     */
//    @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE)
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        System.out.println("goodgoodgood");
        stopWatch.start();
        final int INSERT_NUM = 1000;
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("xiaobin" + i);
            user.setUserAccount("hxb" + i);
            user.setAvatarUrl("https://thirdwx.qlogo.cn/mmopen/vi_32/qeL7fNxa4BXKczghTZBHJAuX7dmCvlhwKY8Jw7c1UycEQ9MKcjRWvDGwNNkk2T1oHwFQTr9hG0aibbhGwkvLvDg/132");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("123");
            user.setEmail("123@qq.com");
            user.setTags("[]");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("11111111");
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
