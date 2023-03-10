package com.xiaobin.yupaobackend.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaobin.yupaobackend.model.domain.User;
import com.xiaobin.yupaobackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.xiaobin.yupaobackend.contant.RedisContant.RECOMMEND_REDISSON_LOCK;
import static com.xiaobin.yupaobackend.contant.RedisContant.RECOMMEND_REDIS_KEY;

/**
 * 缓存预热任务
 *
 * @Author hongxiaobin
 * @Time 2023/3/4-20:38
 */
@Slf4j
@Component
public class PreCacheJob {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UserService userService;
    @Resource
    private RedissonClient redissonClient;

    /**
     * 需要缓存预热的用户ID
     */
    private final List<Long> userIdList = Arrays.asList(1L);
    /**
     * 缓存几页
     */
    private final long pageNum = 1;
    /**
     * 每一页缓存多少数据
     */
    private final long pageSize = 20;

    @Scheduled(cron = "0 0,29 17 * * ? ")
    public void doCacheRecommendUsers() {
        // 加分布式锁，保证只有一个服务器进行缓存预热
        RLock lock = redissonClient.getLock(RECOMMEND_REDISSON_LOCK);
        // 尝试获取锁
        // 参数：等待时间，过期时间，时间单位
        try {
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                for (Long userId : userIdList) {
                    String redisKey = String.format(RECOMMEND_REDIS_KEY + "%s", userId);
                    ValueOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForValue();
                    QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                    // 分页查询
                    Page<User> result = userService.page(new Page<>(pageNum, pageSize), userQueryWrapper);
                    // 写入缓存
                    try {
                        stringObjectValueOperations.set(redisKey, result, 5, TimeUnit.MINUTES);
                        log.info("Cache preheating completed, ID: {}", userId);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("redisson lock error",e);
        }finally {
            // 释放锁，先判断是不是自己的锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
