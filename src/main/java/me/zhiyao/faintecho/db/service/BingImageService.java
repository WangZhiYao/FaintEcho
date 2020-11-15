package me.zhiyao.faintecho.db.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.zhiyao.faintecho.db.mapper.BingImageMapper;
import me.zhiyao.faintecho.db.model.BingImage;
import org.springframework.stereotype.Service;

/**
 * @author WangZhiYao
 * @date 2020/11/15
 */
@Service
public class BingImageService extends ServiceImpl<BingImageMapper, BingImage> {

    public BingImage getBingImage(String endDate) {
        LambdaQueryWrapper<BingImage> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(BingImage::getEndDate, endDate);
        return getOne(wrapper, false);
    }
}
