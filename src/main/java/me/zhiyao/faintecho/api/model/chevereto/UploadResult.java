package me.zhiyao.faintecho.api.model.chevereto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @author WangZhiYao
 * @date 2020/11/21
 */
@Data
public class UploadResult {

    @SerializedName("status_code")
    private int statusCode;

}
