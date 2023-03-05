package com.xiaobin.yupaobackend.service.impl;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @Author hongxiaobin
 * @Time 2023/3/5-16:38
 */
@SpringBootTest
public class RedissonTest {
    @Resource
    private RedissonClient redissonClient;

    @Test
    void redissonTest() {
        // list
        // test-list相当于redis数据的key
        RList<Object> redissonClientList = redissonClient.getList("test-list");
        redissonClientList.add("xiaobin");
        System.out.println(redissonClientList.get(0));
        redissonClientList.remove(0);
        // 其他数据结构和Java集合一样操作
        // map
        RMap<Object, Object> redissonClientMap = redissonClient.getMap("test-map");
        // set
        RSet<Object> redissonClientSet = redissonClient.getSet("test-set");
        // stack
    }
}
