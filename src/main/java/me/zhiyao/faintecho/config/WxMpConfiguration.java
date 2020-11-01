package me.zhiyao.faintecho.config;

import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.redis.JedisWxRedisOps;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import me.chanjar.weixin.mp.config.impl.WxMpRedisConfigImpl;
import me.zhiyao.faintecho.handler.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author WangZhiYao
 * @date 2020/10/23
 */
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(WxMpProperties.class)
public class WxMpConfiguration {

    private final WxMpProperties properties;

    private final LogHandler logHandler;
    private final LocationHandler locationHandler;
    private final MsgHandler msgHandler;
    private final UnsubscribeHandler unsubscribeHandler;
    private final SubscribeHandler subscribeHandler;

    @Bean
    public WxMpService wxMpService() {
        final List<WxMpProperties.MpConfig> configs = properties.getConfigs();
        if (configs == null) {
            throw new RuntimeException("添加相关配置！");
        }

        WxMpService service = new WxMpServiceImpl();
        service.setMultiConfigStorages(configs
                .stream().map(config -> {
                    WxMpDefaultConfigImpl configStorage;
                    if (properties.isUseRedis()) {
                        final WxMpProperties.RedisConfig redisConfig = properties.getRedisConfig();
                        JedisPool jedisPool = new JedisPool(redisConfig.getHost(), redisConfig.getPort());
                        configStorage = new WxMpRedisConfigImpl(new JedisWxRedisOps(jedisPool), config.getAppId());
                    } else {
                        configStorage = new WxMpDefaultConfigImpl();
                    }

                    configStorage.setAppId(config.getAppId());
                    configStorage.setSecret(config.getSecret());
                    configStorage.setToken(config.getToken());
                    configStorage.setAesKey(config.getAesKey());
                    return configStorage;
                }).collect(Collectors.toMap(WxMpDefaultConfigImpl::getAppId, a -> a, (o, n) -> o)));
        return service;
    }

    @Bean
    public WxMpMessageRouter messageRouter(WxMpService wxMpService) {
        final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);

        // 记录所有事件的日志 （异步执行）
        newRouter.rule().handler(logHandler).next();

        // 关注事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.SUBSCRIBE).handler(subscribeHandler).end();

        // 取消关注事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.UNSUBSCRIBE).handler(unsubscribeHandler).end();

        // 接收地理位置消息
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.LOCATION)
                .handler(locationHandler).end();

        // 上报地理位置事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.LOCATION).handler(this.locationHandler).end();

        // 默认
        newRouter.rule().async(false).handler(msgHandler).end();

        return newRouter;
    }
}
