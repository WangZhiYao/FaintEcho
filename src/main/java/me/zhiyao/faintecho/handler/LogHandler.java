package me.zhiyao.faintecho.handler;

import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.zhiyao.faintecho.utils.JsonUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author WangZhiYao
 * @date 2020/10/23
 */
@Component
public class LogHandler extends AbstractHandler {

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {
        logger.debug("\n接收到请求消息，内容：{}", JsonUtils.toJson(wxMessage));
        return null;
    }

}
