package me.zhiyao.faintecho.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author WangZhiYao
 * @date 2020/11/22
 */
@Data
@ConfigurationProperties(prefix = "spring.mail")
public class MailProperties {

    private String host;
    private int port;
    private String username;
    private String password;
    private String from;
    private String to;

}
