package me.zhiyao.faintecho.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author WangZhiYao
 * @date 2020/10/24
 */
@Slf4j
public class LogInterceptor implements ClientHttpRequestInterceptor {

    @NonNull
    @Override
    public ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body,
                                        @NonNull ClientHttpRequestExecution execution) throws IOException {
        traceRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        traceResponse(response);
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) {
        log.info("==========================【请求开始】==========================");
        log.debug("URI         : {}", request.getURI());
        log.debug("Method      : {}", request.getMethod());
        log.debug("Headers     : {}", request.getHeaders());
        log.debug("Request body: {}", new String(body, StandardCharsets.UTF_8));
        log.info("==========================【请求结束】==========================");
    }

    private void traceResponse(ClientHttpResponse response) throws IOException {
        if (response.getHeaders().getContentType() != null) {
            MediaType contentType = response.getHeaders().getContentType();
            if (contentType.isCompatibleWith(MediaType.IMAGE_JPEG)) {
                return;
            }
        }

        StringBuilder inputStringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(response.getBody(), StandardCharsets.UTF_8)
        );
        String line = bufferedReader.readLine();
        while (line != null) {
            inputStringBuilder.append(line);
            inputStringBuilder.append('\n');
            line = bufferedReader.readLine();
        }
        log.info("==========================【响应开始】==========================");
        log.debug("Status code  : {}", response.getStatusCode());
        log.debug("Status text  : {}", response.getStatusText());
        log.debug("Headers      : {}", response.getHeaders());
        log.debug("Response body: {}", inputStringBuilder.toString());
        log.info("==========================【响应结束】==========================");
    }
}
