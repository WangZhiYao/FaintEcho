package me.zhiyao.faintecho.api.model.qweather;

import lombok.Data;

import java.util.Date;

/**
 * @author WangZhiYao
 * @date 2020/10/24
 */
@Data
public class LiveWeather {

    private String code;
    private Date updateTime;
    private String fxLink;
    private Now now;
    private Refer refer;

}
