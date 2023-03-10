package com.xiaobin.yupaobackend.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置
 *
 * @Author hongxiaobin
 * @Time 2023/3/5-16:25
 */
@Configuration
/*
  要读取配置类的那个前缀的数据
 */
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {
    private String host;
    private String port;
    private String password;
    private String redissonDataBase;

    @Bean
    public RedissonClient redissonClient() {
        // 1. 配置Redis
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s", host, port);
        config.useSingleServer()
                .setAddress(redisAddress)
                .setDatabase(Integer.parseInt(redissonDataBase))
                .setPassword(password);

        // 2. 创建实例
        // Sync and Async API
        return Redisson.create(config);
    }
}
