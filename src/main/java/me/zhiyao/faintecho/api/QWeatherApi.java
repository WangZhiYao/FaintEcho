package me.zhiyao.faintecho.api;

import me.zhiyao.faintecho.api.model.qweather.LiveWeather;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.concurrent.CompletableFuture;

/**
 * @author WangZhiYao
 * @date 2020/11/14
 */
public interface QWeatherApi {

    @GET("v7/weather/now")
    CompletableFuture<LiveWeather> getWeather(@Query("location") String location);
}
