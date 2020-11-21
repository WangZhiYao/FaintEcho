package me.zhiyao.faintecho.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * @author WangZhiYao
 * @date 2020/11/21
 */
@Slf4j
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class MailConfiguration {

    private final MailProperties mMailProperties;

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mMailProperties.getHost());
        mailSender.setPort(mMailProperties.getPort());
        mailSender.setUsername(mMailProperties.getUsername());
        mailSender.setPassword(mMailProperties.getPassword());

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");//开启认证
        properties.setProperty("mail.smtp.port", Integer.toString(mMailProperties.getPort()));//设置端口
        properties.setProperty("mail.smtp.socketFactory.port", Integer.toString(mMailProperties.getPort()));//设置ssl端口
        properties.setProperty("mail.smtp.socketFactory.fallback", "false");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        mailSender.setJavaMailProperties(properties);

        return mailSender;
    }
}
