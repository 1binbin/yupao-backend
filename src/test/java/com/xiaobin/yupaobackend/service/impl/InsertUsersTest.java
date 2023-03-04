package com.xiaobin.yupaobackend.service.impl;

import com.xiaobin.yupaobackend.model.domain.User;
import com.xiaobin.yupaobackend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class InsertUsersTest {

    @Resource
    private UserService userService;

    private ExecutorService executorService = new ThreadPoolExecutor(40, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    /**
     * 批量插入用户
     */
    @Test
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        List<User> userList = new ArrayList<>();
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
            userList.add(user);
        }
        // 20 秒 10 万条
        userService.saveBatch(userList, 10000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    /**
     * 并发批量插入用户
     */
    @Test
    public void doConcurrencyInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 分十组
        int batchSize = 5000;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        // 控制线程数
        for (int i = 0; i < 100; i++) {
            List<User> userList = new ArrayList<>();
            do {
                j++;
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
                userList.add(user);
            } while (j % batchSize != 0);
            // 异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("threadName: " + Thread.currentThread().getName());
                userService.saveBatch(userList, batchSize);
            }, executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        // 20 秒 10 万条
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
