package me.zhiyao.faintecho.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author WangZhiYao
 * @date 2020/10/24
 */
@Data
@ConfigurationProperties(prefix = "qweather")
public class QWeatherProperties {

    private String key;
}
