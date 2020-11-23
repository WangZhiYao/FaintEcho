package me.zhiyao.faintecho.api.response;

import lombok.Data;

/**
 * @author WangZhiYao
 * @date 2020/11/24
 */
@Data
public class BaseResponse<T> {

    private int ret;
    private String msg;
    private T data;
}
