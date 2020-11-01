package me.zhiyao.faintecho.handler;

import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author WangZhiYao
 * @date 2020/10/23
 */
public abstract class AbstractHandler implements WxMpMessageHandler {

    protected Logger logger = LoggerFactory.getLogger(getClass());

}
