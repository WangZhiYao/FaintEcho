package me.zhiyao.faintecho;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author WangZhiYao
 * @date 2020/10/23
 */
@SpringBootApplication
@EnableScheduling
public class FaintEchoApplication {

    public static void main(String[] args) {
        SpringApplication.run(FaintEchoApplication.class, args);
    }

}
