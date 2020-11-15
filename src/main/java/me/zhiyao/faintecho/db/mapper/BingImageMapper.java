package me.zhiyao.faintecho.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.zhiyao.faintecho.db.model.BingImage;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author WangZhiYao
 * @date 2020/11/15
 */
@Mapper
@Repository
public interface BingImageMapper extends BaseMapper<BingImage> {

}
