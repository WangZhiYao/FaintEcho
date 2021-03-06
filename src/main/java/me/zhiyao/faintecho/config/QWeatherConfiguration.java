package me.zhiyao.faintecho.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhiyao.faintecho.api.QWeatherApi;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author WangZhiYao
 * @date 2020/11/15
 */
@Slf4j
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(QWeatherProperties.class)
public class QWeatherConfiguration {

    private static final String QWEATHER_BASE_URL = "https://devapi.qweather.com/";

    private final QWeatherProperties mQWeatherProperties;

    private final OkHttpClient okHttpClient;

    @Bean
    public QWeatherApi qWeatherApi() {
        return new Retrofit.Builder()
                .client(qWeatherOkHttpClient())
                .baseUrl(QWEATHER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QWeatherApi.class);
    }

    @Bean
    public OkHttpClient qWeatherOkHttpClient() {
        return okHttpClient.newBuilder()
                .addInterceptor(chain -> {
                    Request request = chain.request();

                    HttpUrl requestUrl = request.url()
                            .newBuilder()
                            .addQueryParameter("key", mQWeatherProperties.getKey())
                            .build();

                    request = request.newBuilder()
                            .url(requestUrl)
                            .build();

                    return chain.proceed(request);
                })
                .build();
    }
}
