package me.zhiyao.faintecho.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhiyao.faintecho.api.CheveretoApi;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author WangZhiYao
 * @date 2020/11/21
 */
@Slf4j
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(CheveretoProperties.class)
public class CheveretoConfiguration {

    private static final String CHEVERETO_BASE_URL = "https://img.339.im/";

    private final CheveretoProperties mCheveretoProperties;

    private final OkHttpClient okHttpClient;

    @Bean
    public CheveretoApi cheveretoApi() {
        return new Retrofit.Builder()
                .client(cheveretoOkHttpClient())
                .baseUrl(CHEVERETO_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CheveretoApi.class);
    }

    @Bean
    public OkHttpClient cheveretoOkHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(log::info);
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        return okHttpClient.newBuilder()
                .addInterceptor(chain -> {
                    Request request = chain.request();

                    HttpUrl requestUrl = request.url()
                            .newBuilder()
                            .addQueryParameter("key", mCheveretoProperties.getKey())
                            .build();

                    String requestUrlStr = requestUrl.toString()
                            .replace("%3F", "?")
                            .replace("%3D", "=")
                            .replace("%26", "&");

                    request = request.newBuilder()
                            .url(requestUrlStr)
                            .build();

                    return chain.proceed(request);
                })
                .addInterceptor(loggingInterceptor)
                .build();
    }
}
