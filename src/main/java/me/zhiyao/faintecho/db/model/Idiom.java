package me.zhiyao.faintecho.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author WangZhiYao
 * @date 2020/10/31
 */
@Data
public class Idiom {

    @TableId(type = IdType.AUTO)
    private Integer idiomId;
    private String value;
    private String pinyin;
    private String startChar;
    private String endChar;
    private String startPinyin;
    private String endPinyin;
    private String paraphrase;
    private String source;
    private String example;
}
