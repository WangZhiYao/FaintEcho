package me.zhiyao.faintecho.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.zhiyao.faintecho.api.TencentApi;
import me.zhiyao.faintecho.api.model.tencent.TextChat;
import me.zhiyao.faintecho.api.response.BaseResponse;
import me.zhiyao.faintecho.builder.TextBuilder;
import org.springframework.stereotype.Service;

/**
 * @author WangZhiYao
 * @date 2020/11/24
 */
@Slf4j
@Service
@AllArgsConstructor
public class TextChatService {

    private final TencentApi mTencentApi;

    public WxMpXmlOutMessage textChat(WxMpXmlMessage wxMessage, WxMpService wxMpService) {
        try {
            BaseResponse<TextChat> response = mTencentApi.textChat(wxMessage.getFromUser(), wxMessage.getContent())
                    .get();
            if (response.getRet() == 0) {
                return new TextBuilder().build(response.getData().getAnswer(), wxMessage, wxMpService);
            } else {
                return new TextBuilder().build(response.getMsg(), wxMessage, wxMpService);
            }
        } catch (Exception ex) {
            log.error("text chat error: ", ex);
        }
        return new TextBuilder().build("服务器开小差了，请稍后再试", wxMessage, wxMpService);
    }
}
