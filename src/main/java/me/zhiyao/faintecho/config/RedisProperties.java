package me.zhiyao.faintecho.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author WangZhiYao
 * @date 2020/11/21
 */
@Data
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {

    private String host;
    private int port;
}
