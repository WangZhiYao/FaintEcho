package me.zhiyao.faintecho.utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhiyao.faintecho.config.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * @author WangZhiYao
 * @date 2020/11/22
 */
@Slf4j
@Component
@AllArgsConstructor
public class MailUtils {

    private final JavaMailSender mailSender;
    private final MailProperties mMailProperties;

    public void send(String subject, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mMailProperties.getFrom());
        mailMessage.setTo(mMailProperties.getTo());
        mailMessage.setSubject(subject);
        mailMessage.setText(text);

        try {
            mailSender.send(mailMessage);
        } catch (Exception ex) {
            log.error("can not send mail: ", ex);
        }
    }
}
