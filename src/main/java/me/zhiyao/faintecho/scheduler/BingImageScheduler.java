package me.zhiyao.faintecho.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.mp.api.WxMpService;
import me.zhiyao.faintecho.config.BingConfiguration;
import me.zhiyao.faintecho.constants.CacheKey;
import me.zhiyao.faintecho.db.model.BingImage;
import me.zhiyao.faintecho.db.service.BingImageService;
import me.zhiyao.faintecho.service.BingService;
import me.zhiyao.faintecho.service.CheveretoService;
import me.zhiyao.faintecho.utils.MailUtils;
import me.zhiyao.faintecho.utils.RedisUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * @author WangZhiYao
 * @date 2020/11/17
 */
@Slf4j
@Component
@AllArgsConstructor
public class BingImageScheduler {

    private final BingService mBingService;
    private final RedisUtils mRedisUtils;
    private final WxMpService mWxMpService;
    private final BingImageService mBingImageService;
    private final CheveretoService mCheveretoService;
    private final MailUtils mMailUtils;

    @Scheduled(cron = "0 5 0 * * ?")
    public void getBingImage() {
        LocalDate todayDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;
        String today = todayDate.format(formatter);

        BingImage bingImage = mRedisUtils.getObject(CacheKey.PREFIX_BING_IMAGE + today, BingImage.class);
        if (bingImage == null) {
            bingImage = mBingService.getHPImageArchiveFromApi();
            if (bingImage != null) {
                WxMediaUploadResult uploadResult = mBingService.uploadBingImage(bingImage, mWxMpService);
                if (uploadResult == null) {
                    log.error("上传 Bing 每日图片到微信服务器失败");
                } else {
                    bingImage.setMediaId(uploadResult.getMediaId());
                    bingImage.setUploadTime(todayDate.format(DateTimeFormatter.BASIC_ISO_DATE));
                }

                mBingImageService.save(bingImage);
                mRedisUtils.setObject(CacheKey.PREFIX_BING_IMAGE + today, bingImage);
                mRedisUtils.expire(CacheKey.PREFIX_BING_IMAGE + today, (int) TimeUnit.DAYS.toSeconds(3));

                mCheveretoService.upload(BingConfiguration.BING_BASE_URL + bingImage.getUrl().substring(1));
            } else {
                log.error("自动获取 Bing 每日图片失败");
                mMailUtils.send("自动获取 Bing 每日图片失败", "");
            }
        }
    }
}
