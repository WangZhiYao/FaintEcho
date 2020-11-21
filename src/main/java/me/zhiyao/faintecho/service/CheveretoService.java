package me.zhiyao.faintecho.service;

/**
 * @author WangZhiYao
 * @date 2020/11/21
 */

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhiyao.faintecho.api.CheveretoApi;
import me.zhiyao.faintecho.utils.MailUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CheveretoService {

    private final CheveretoApi mCheveretoApi;
    private final MailUtils mMailUtils;

    public void upload(String url) {
        mCheveretoApi.upload(url, "json")
                .thenAcceptAsync(uploadResult -> {
                    if (uploadResult.getStatusCode() != 200) {
                        mMailUtils.send("自动上传Bing每日图片到Chevereto失败", uploadResult.toString());
                    }
                })
                .exceptionally(throwable -> {
                    mMailUtils.send("自动上传Bing每日图片到Chevereto失败", throwable.toString());
                    log.error("图片上传失败：", throwable);
                    return null;
                });
    }
}
