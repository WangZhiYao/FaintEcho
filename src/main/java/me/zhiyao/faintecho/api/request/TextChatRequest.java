package me.zhiyao.faintecho.api.request;

import lombok.Data;

/**
 * @author WangZhiYao
 * @date 2020/11/23
 */
@Data
public class TextChatRequest {

    private String session;
    private String question;

    public TextChatRequest(String session, String question) {
        this.session = session;
        this.question = question;
    }
}
