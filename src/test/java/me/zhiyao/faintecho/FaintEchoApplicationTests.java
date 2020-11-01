package me.zhiyao.faintecho;

import me.zhiyao.faintecho.db.service.IdiomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FaintEchoApplicationTests {

    @Autowired
    private IdiomService mIdiomService;

    @Test
    void contextLoads() {

    }

}
