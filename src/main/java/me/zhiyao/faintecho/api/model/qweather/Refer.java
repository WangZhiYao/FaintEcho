package me.zhiyao.faintecho.api.model.qweather;

import lombok.Data;

import java.util.List;

/**
 * @author WangZhiYao
 * @date 2020/11/14
 */
@Data
public class Refer {

    private List<String> sources;
    private List<String> license;

}
