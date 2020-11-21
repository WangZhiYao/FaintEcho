package me.zhiyao.faintecho.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

/**
 * @author WangZhiYao
 * @date 2020/11/21
 */
@Slf4j
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfiguration {

    private final RedisProperties redisProperties;

    @Bean
    public JedisPool jedisPool() {
        return new JedisPool(redisProperties.getHost(), redisProperties.getPort());
    }
}
