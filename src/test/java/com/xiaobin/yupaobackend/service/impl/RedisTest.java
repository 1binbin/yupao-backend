package com.xiaobin.yupaobackend.service.impl;

import com.xiaobin.yupaobackend.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

/**
 * Redis增删改查测试
 * @Author hongxiaobin
 * @Time 2023/3/4-18:57
 */
@SpringBootTest
public class RedisTest {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Test
    void test(){
        // 得到String类型的操作合集
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 增
        valueOperations.set("xiaobinString","dog");
        valueOperations.set("xiaobinInt",1);
        valueOperations.set("xiaobinUser",new User());
        // 查
        String xiaobinString = (String) valueOperations.get("xiaobinString");
        System.out.println(xiaobinString);
        // 改——重新set即可
        // 删
        redisTemplate.delete("xiaobinInt");
    }
}
