package me.zhiyao.faintecho.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.zhiyao.faintecho.db.model.Idiom;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author WangZhiYao
 * @date 2020/10/31
 */
@Mapper
@Repository
public interface IdiomMapper extends BaseMapper<Idiom> {

}
