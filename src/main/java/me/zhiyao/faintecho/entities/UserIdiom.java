package me.zhiyao.faintecho.entities;

import lombok.Data;

/**
 * @author WangZhiYao
 * @date 2020/10/31
 */
@Data
public class UserIdiom {

    private String user;
    private String idiom;

    public String lastWords() {
        return idiom.substring(idiom.length() - 1);
    }
}
