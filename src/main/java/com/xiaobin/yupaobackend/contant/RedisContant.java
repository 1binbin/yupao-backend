package com.xiaobin.yupaobackend.contant;

/**
 * Redis中key前缀常量
 * @Author hongxiaobin
 * @Time 2023/3/4-19:50
 */
public interface RedisContant {
    /**
     * 推荐信息缓存key前缀
     */
    String RECOMMEND_REDIS_KEY = "yupao:user:recommend:";

    /**
     * 定时任务分布式锁
     */
    String RECOMMEND_REDISSON_LOCK = "yupao:precachejob:docache:lock";


    /**
     * 加入队伍分布式锁
     */
    String JOINTEAM_REDISSON_LOCK = "yupao:teamserviceimpl:docache:lock";


}
