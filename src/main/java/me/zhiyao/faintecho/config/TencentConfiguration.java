package me.zhiyao.faintecho.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhiyao.faintecho.api.TencentApi;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.DigestUtils;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @author WangZhiYao
 * @date 2020/11/23
 */
@Slf4j
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(TencentProperties.class)
public class TencentConfiguration {

    private static final String TENCENT_BASE_URL = "https://api.ai.qq.com/";

    private static final String MEDIA_TYPE = "application/json; charset=utf-8";

    private final TencentProperties mTencentProperties;

    private final OkHttpClient okHttpClient;

    @Bean
    public TencentApi tencentApi() {
        return new Retrofit.Builder()
                .client(tencentOkHttpClient())
                .baseUrl(TENCENT_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TencentApi.class);
    }

    @Bean
    public OkHttpClient tencentOkHttpClient() {
        return okHttpClient.newBuilder()
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    try {
                        request = signRequest(request);
                    } catch (Exception ex) {
                        log.error("sign request failed.", ex);
                    }
                    return chain.proceed(request);
                })
                .build();
    }

    private Request signRequest(Request request) throws IOException, JSONException {
        RequestBody requestBody = request.body();
        FormBody.Builder newRequestBodyBuilder = new FormBody.Builder();

        if (requestBody != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            String requestBodyStr = buffer.readUtf8();

            if (!StringUtils.isBlank(requestBodyStr)) {
                String[] params = requestBodyStr.split("&");

                Map<String, Object> paramsMap = makeParamsMap();
                for (String param : params) {
                    String[] entry = param.split("=");
                    String key = entry[0];
                    String value = entry[1];
                    paramsMap.put(key, value);
                }

                if (!paramsMap.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
                        try {
                            sb.append(entry.getKey())
                                    .append("=")
                                    .append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"))
                                    .append("&");
                        } catch (UnsupportedEncodingException ex) {
                            log.error("url encoder failed: " + entry.getValue(), ex);
                        }

                        newRequestBodyBuilder.add(entry.getKey(), entry.getValue().toString());
                    }

                    sb.append("app_key")
                            .append("=")
                            .append(mTencentProperties.getAppKey());

                    String sign = DigestUtils.md5DigestAsHex(sb.toString().getBytes(StandardCharsets.UTF_8)).toUpperCase();
                    newRequestBodyBuilder.add("sign", sign);
                }

                requestBody = newRequestBodyBuilder.build();
            }

            request = request.newBuilder()
                    .post(requestBody)
                    .build();
        }

        return request;
    }

    private Map<String, Object> makeParamsMap() {
        long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        TreeMap<String, Object> paramsMap = new TreeMap<>();
        paramsMap.put("app_id", mTencentProperties.getAppId());
        paramsMap.put("nonce_str", UUID.randomUUID().toString().split("-")[4]);
        paramsMap.put("time_stamp", timestamp);
        return paramsMap;
    }

}
