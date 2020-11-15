package me.zhiyao.faintecho.api.model.bing;

import lombok.Data;

import java.util.List;

/**
 * @author WangZhiYao
 * @date 2020/11/14
 */
@Data
public class HPImageArchive {

    private List<Image> images;
    private Tooltips tooltips;

}
