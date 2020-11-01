package me.zhiyao.faintecho.db.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.zhiyao.faintecho.db.mapper.IdiomMapper;
import me.zhiyao.faintecho.db.model.Idiom;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author WangZhiYao
 * @date 2020/10/31
 */
@Service
public class IdiomService extends ServiceImpl<IdiomMapper, Idiom> {

    public Idiom getIdiom(String content) {
        LambdaQueryWrapper<Idiom> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Idiom::getValue, content)
                .last("LIMIT 1");
        return getOne(wrapper);
    }

    public List<Idiom> getIdiomByStartPinyin(String startPinyin) {
        LambdaQueryWrapper<Idiom> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Idiom::getStartPinyin, startPinyin);
        return list(wrapper);
    }

    public List<Idiom> getIdiomByStartCharAndPinyin(String startChar, String startPinyin) {
        LambdaQueryWrapper<Idiom> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Idiom::getStartChar, startChar)
                .eq(Idiom::getStartPinyin, startPinyin);
        return list(wrapper);
    }
}
