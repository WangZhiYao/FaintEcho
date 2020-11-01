package me.zhiyao.faintecho.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.lang.NonNull;
import org.springframework.util.function.SingletonSupplier;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

/**
 * @author WangZhiYao
 * @date 2020/10/24
 */
@Slf4j
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(QWeatherProperties.class)
public class QWeatherConfiguration {

    private final QWeatherProperties qWeatherProperties;

    @Bean
    public RestTemplate qWeatherRestTemplate(RestTemplateBuilder builder) {
        return builder.requestFactory(
                SingletonSupplier.of(
                        new BufferingClientHttpRequestFactory(
                                new HttpComponentsClientHttpRequestFactory()
                        )
                )
        )
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .additionalInterceptors(qWeatherAuthInterceptor(), new LogInterceptor())
                .additionalInterceptors()
                .build();
    }

    @Bean
    public ClientHttpRequestInterceptor qWeatherAuthInterceptor() {
        return new ClientHttpRequestInterceptor() {
            @NonNull
            @Override
            public ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body,
                                                @NonNull ClientHttpRequestExecution execution) throws IOException {

                URI uri = UriComponentsBuilder.fromHttpRequest(request)
                        .queryParam("key", qWeatherProperties.getKey())
                        .build()
                        .toUri();

                HttpRequest newRequest = new HttpRequestWrapper(request) {
                    @NonNull
                    @Override
                    public URI getURI() {
                        return uri;
                    }
                };

                return execution.execute(newRequest, body);
            }
        };
    }
}
