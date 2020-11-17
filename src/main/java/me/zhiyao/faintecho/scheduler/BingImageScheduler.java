package me.zhiyao.faintecho.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhiyao.faintecho.db.model.BingImage;
import me.zhiyao.faintecho.db.service.BingImageService;
import me.zhiyao.faintecho.service.BingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author WangZhiYao
 * @date 2020/11/17
 */
@Slf4j
@Component
@AllArgsConstructor
public class BingImageScheduler {

    private final BingService mBingService;
    private final BingImageService mBingImageService;

    @Scheduled(cron = "0 10 0 * * ?")
    public void getBingImage() {
        BingImage bingImage = mBingService.getHPImageArchiveFromApi();
        if (bingImage != null) {
            try {
                mBingImageService.save(bingImage);
            } catch (Exception ex) {
                log.error("保存 Bing 每日图片失败", ex);
            }
        } else {
            log.error("自动获取 Bing 每日图片失败");
        }
    }

}
