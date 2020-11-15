package me.zhiyao.faintecho.handler;

import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.zhiyao.faintecho.builder.TextBuilder;
import me.zhiyao.faintecho.service.BingService;
import me.zhiyao.faintecho.service.IdiomSolitaireService;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author WangZhiYao
 * @date 2020/10/23
 */
@Component
@AllArgsConstructor
public class MsgHandler extends AbstractHandler {

    private final BingService mBingService;
    private final IdiomSolitaireService mIdiomSolitaireService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {

        if (wxMessage.getContent().toLowerCase().contains("bing")) {
            return mBingService.getHPImageArchive(wxMessage, wxMpService);
        }

        if (mIdiomSolitaireService.isUserActiveIdiomModel(wxMessage)) {
            return mIdiomSolitaireService.handle(wxMessage, wxMpService);
        }

        if (mIdiomSolitaireService.isUserIdiomModel(wxMessage.getFromUser())) {
            return mIdiomSolitaireService.idiomSolitaire(wxMessage, wxMpService);
        }

        String content = "你是傻的";
        return new TextBuilder().build(content, wxMessage, wxMpService);
    }

}
