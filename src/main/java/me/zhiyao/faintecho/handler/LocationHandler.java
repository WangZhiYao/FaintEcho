package me.zhiyao.faintecho.handler;

import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.zhiyao.faintecho.builder.TextBuilder;
import me.zhiyao.faintecho.entities.LiveWeather;
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

    private final WeatherService weatherService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {

        if (wxMessage.getMsgType().equals(WxConsts.XmlMsgType.LOCATION)) {
            //TODO 接收处理用户发送的地理位置消息
            try {
                LiveWeather liveWeather = weatherService.getWeather(wxMessage.getLocationY(), wxMessage.getLocationX());
                String content;
                if (liveWeather != null) {
                    content = String.format("该地点目前天气 %s，气温 %s℃，体感温度 %s℃。", liveWeather.getNow().getText(),
                            liveWeather.getNow().getTemp(), liveWeather.getNow().getFeelsLike());
                } else {
                    content = "获取天气信息失败。";
                }
                return new TextBuilder().build(content, wxMessage, null);
            } catch (Exception ex) {
                logger.error("位置消息接收处理失败", ex);
                return null;
            }
        }

        //上报地理位置事件
        logger.debug("上报地理位置，纬度 : {}，经度 : {}，精度 : {}",
                wxMessage.getLatitude(), wxMessage.getLongitude(), wxMessage.getPrecision());

        //TODO  可以将用户地理位置信息保存到本地数据库，以便以后使用

        return null;
    }
}
