package me.zhiyao.faintecho.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.zhiyao.faintecho.api.QWeatherApi;
import me.zhiyao.faintecho.api.model.qweather.LiveWeather;
import me.zhiyao.faintecho.builder.TextBuilder;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;

/**
 * @author WangZhiYao
 * @date 2020/10/24
 */
@Slf4j
@AllArgsConstructor
@Service
public class WeatherService {

    private final QWeatherApi mQWeatherApi;

    public WxMpXmlOutMessage getWeather(WxMpXmlMessage wxMessage, WxMpService wxMpService) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);

        LiveWeather liveWeather = null;
        try {
            liveWeather = mQWeatherApi.getWeather(String.format("%s,%s", nf.format(wxMessage.getLocationY()),
                    nf.format(wxMessage.getLocationX())))
                    .get();
        } catch (Exception ex) {
            log.error("获取天气信息失败", ex);
        }

        if (liveWeather == null) {
            return new TextBuilder().build("获取该地点天气信息失败", wxMessage, wxMpService);
        }

        return new TextBuilder().build(String.format("该地点目前天气 %s，气温 %s℃，体感温度 %s℃。",
                liveWeather.getNow().getText(),
                liveWeather.getNow().getTemp(),
                liveWeather.getNow().getFeelsLike()),
                wxMessage, wxMpService);
    }
}
