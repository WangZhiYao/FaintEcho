package me.zhiyao.faintecho.handler;

import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.zhiyao.faintecho.service.WeatherService;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author WangZhiYao
 * @date 2020/10/23
 */
@Component
@AllArgsConstructor
public class LocationHandler extends AbstractHandler {

    private final WeatherService mWeatherService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {

        if (wxMessage.getMsgType().equals(WxConsts.XmlMsgType.LOCATION)) {
            return mWeatherService.getWeather(wxMessage, wxMpService);
        }

        //上报地理位置事件
        logger.debug("上报地理位置，纬度 : {}，经度 : {}，精度 : {}",
                wxMessage.getLatitude(), wxMessage.getLongitude(), wxMessage.getPrecision());

        //TODO  可以将用户地理位置信息保存到本地数据库，以便以后使用

        return null;
    }
}
