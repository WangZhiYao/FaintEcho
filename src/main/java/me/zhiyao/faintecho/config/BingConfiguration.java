package me.zhiyao.faintecho.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhiyao.faintecho.api.BingApi;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author WangZhiYao
 * @date 2020/11/14
 */
@Slf4j
@AllArgsConstructor
@Configuration
public class BingConfiguration {

    private static final String BING_BASE_URL = "https://cn.bing.com/";

    @Bean
    public BingApi bingApi() {
        return new Retrofit.Builder()
                .client(bingOkHttpClient())
                .baseUrl(BING_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BingApi.class);
    }

    @Bean
    public OkHttpClient bingOkHttpClient() {
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    HttpUrl requestUrl = request.url();
                    String requestUrlStr = requestUrl.toString()
                            .replace("%3F", "?");

                    request = request.newBuilder()
                            .url(requestUrlStr)
                            .build();

                    return chain.proceed(request);
                })
                .build();
    }

}
