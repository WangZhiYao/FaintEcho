package me.zhiyao.faintecho.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMaterialService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpMaterialServiceImpl;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.zhiyao.faintecho.api.BingApi;
import me.zhiyao.faintecho.api.model.bing.HPImageArchive;
import me.zhiyao.faintecho.api.model.bing.Image;
import me.zhiyao.faintecho.builder.ImageBuilder;
import me.zhiyao.faintecho.builder.TextBuilder;
import me.zhiyao.faintecho.builder.VideoBuilder;
import me.zhiyao.faintecho.constants.CacheKey;
import me.zhiyao.faintecho.db.model.BingImage;
import me.zhiyao.faintecho.db.service.BingImageService;
import me.zhiyao.faintecho.utils.RedisUtils;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * @author WangZhiYao
 * @date 2020/11/14
 */
@Slf4j
@AllArgsConstructor
@Service
public class BingService {

    private static final int IMAGE_TYPE_IMAGE = 1;
    private static final int IMAGE_TYPE_VIDEO = 2;

    private static final int WX_MATERIAL_EXPIRED_DAYS = 3;

    private final BingApi mBingApi;

    private final RedisUtils mRedisUtils;
    private final BingImageService mBingImageService;

    public WxMpXmlOutMessage getHPImageArchive(WxMpXmlMessage wxMessage, WxMpService wxMpService) {
        String content = wxMessage.getContent();
        String[] params = content.split(",");
        String endDate = null;
        if (params.length > 1) {
            endDate = content.split(",")[1];
        }

        LocalDate todayDate = LocalDate.now();

        if (StringUtils.isBlank(endDate)) {
            DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;
            endDate = todayDate.format(formatter);
        }

        if (!StringUtils.isNumeric(endDate) && endDate.length() != 8) {
            return new TextBuilder().build("输入的日期有误，格式应为：20201115", wxMessage, wxMpService);
        }

        LocalDate specifyDate = LocalDate.parse(endDate, DateTimeFormatter.BASIC_ISO_DATE);
        if (specifyDate.isAfter(todayDate)) {
            return new TextBuilder().build("你是穿越回来的吗？", wxMessage, wxMpService);
        }

        BingImage bingImage = mRedisUtils.getObject(CacheKey.PREFIX_BING_IMAGE + endDate, BingImage.class);

        if (bingImage == null) {
            bingImage = mBingImageService.getBingImage(endDate);
            if (bingImage == null && specifyDate.isEqual(todayDate)) {
                bingImage = getHPImageArchiveFromApi();
            }

            if (bingImage != null) {
                mRedisUtils.setObject(CacheKey.PREFIX_BING_IMAGE + endDate, bingImage);
                mRedisUtils.expire(CacheKey.PREFIX_BING_IMAGE + endDate, (int) TimeUnit.DAYS.toSeconds(3));
            }
        }

        if (bingImage == null) {
            return new TextBuilder().build("无法获取" + endDate + "的 Bing 每日图片", wxMessage, wxMpService);
        }

        boolean needUpload = true;
        if (!StringUtils.isBlank(bingImage.getUploadTime())) {
            LocalDate uploadDate = LocalDate.parse(bingImage.getUploadTime(), DateTimeFormatter.BASIC_ISO_DATE);
            if (uploadDate.until(todayDate, ChronoUnit.DAYS) <= WX_MATERIAL_EXPIRED_DAYS) {
                needUpload = false;
            }
        }

        if (needUpload) {
            WxMediaUploadResult uploadResult = uploadBingImage(bingImage, wxMpService);
            if (uploadResult == null) {
                return new TextBuilder().build(String.format("%s%s", "上传 Bing 每日图片到微信服务器失败，" +
                        "原图地址为：https://cn.bing.com", bingImage.getUrl()), wxMessage, wxMpService);
            } else {
                bingImage.setMediaId(uploadResult.getMediaId());
                bingImage.setUploadTime(todayDate.format(DateTimeFormatter.BASIC_ISO_DATE));
                mBingImageService.saveOrUpdate(bingImage);
                mRedisUtils.setObject(CacheKey.PREFIX_BING_IMAGE + endDate, bingImage);
                mRedisUtils.expire(CacheKey.PREFIX_BING_IMAGE + endDate, (int) TimeUnit.DAYS.toSeconds(3));
            }
        }

        if (bingImage.getType() == IMAGE_TYPE_IMAGE) {
            return new ImageBuilder().build(bingImage.getMediaId(), wxMessage, wxMpService);
        } else {
            return new VideoBuilder().build(bingImage.getMediaId(), wxMessage, wxMpService);
        }
    }

    public BingImage getHPImageArchiveFromApi() {
        HPImageArchive hpImageArchive = null;
        try {
            hpImageArchive = mBingApi.getHPImageArchive("js", 0, 1, System.currentTimeMillis(), "hp")
                    .get();
        } catch (Exception ex) {
            log.error("获取 Bing 每日图片失败", ex);
        }

        if (hpImageArchive == null) {
            return null;
        }

        ResponseBody responseBody = null;
        try {
            responseBody = mBingApi.download(hpImageArchive.getImages().get(0).getUrl().substring(1))
                    .get();
        } catch (Exception ex) {
            log.error("下载 Bing 每日图片失败", ex);
        }

        if (responseBody == null) {
            return null;
        }

        Image image = hpImageArchive.getImages().get(0);

        String fileName = null;
        try {
            fileName = image.getUrl().split("&")[0].split("=")[1];
        } catch (Exception ex) {
            log.error("截取 Bing 每日图片文件名失败：" + image.getUrl(), ex);
        }

        if (fileName == null) {
            fileName = image.getHsh() + ".jpg";
        }

        File imageFile = null;

        try {
            imageFile = Files.write(Paths.get("/usr/local/bing", fileName), responseBody.bytes(),
                    StandardOpenOption.CREATE)
                    .toFile();
        } catch (Exception ex) {
            log.error("创建 Bing 每日图片失败", ex);
        }

        if (imageFile == null) {
            return null;
        }

        BingImage bingImage = new BingImage();
        bingImage.setStartDate(image.getStartDate());
        bingImage.setFullStartDate(image.getFullStartDate());
        bingImage.setEndDate(image.getEndDate());
        bingImage.setUrl(image.getUrl());
        bingImage.setCopyright(image.getCopyright());
        bingImage.setLocalPath(imageFile.getAbsolutePath());

        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            if (contentType.type().contains("image")) {
                bingImage.setType(IMAGE_TYPE_IMAGE);
            } else {
                bingImage.setType(IMAGE_TYPE_VIDEO);
            }
        } else if (image.getUrl().toLowerCase().contains(".jpg")) {
            bingImage.setType(IMAGE_TYPE_IMAGE);
        } else {
            bingImage.setType(IMAGE_TYPE_IMAGE);
        }

        return bingImage;
    }

    public WxMediaUploadResult uploadBingImage(BingImage bingImage, WxMpService wxMpService) {
        WxMediaUploadResult uploadResult = null;
        try {
            File imageFile = new File(bingImage.getLocalPath());
            if (imageFile.exists()) {
                WxMpMaterialService wxMpMaterialService = new WxMpMaterialServiceImpl(wxMpService);
                if (bingImage.getType() == IMAGE_TYPE_IMAGE) {
                    uploadResult = wxMpMaterialService
                            .mediaUpload(WxConsts.MediaFileType.IMAGE, imageFile);
                } else {
                    uploadResult = wxMpMaterialService
                            .mediaUpload(WxConsts.MediaFileType.VIDEO, imageFile);
                }
            }
        } catch (WxErrorException ex) {
            log.error("上传 Bing 每日图片失败", ex);
        }
        return uploadResult;
    }
}
