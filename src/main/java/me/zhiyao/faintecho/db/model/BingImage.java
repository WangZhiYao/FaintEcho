package me.zhiyao.faintecho.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author WangZhiYao
 * @date 2020/11/15
 */
@Data
public class BingImage {

    @TableId(type = IdType.AUTO)
    private Integer bingImageId;
    private String startDate;
    private String fullStartDate;
    private String endDate;
    private String url;
    private String copyright;
    private String localPath;
    private String mediaId;
    private int type;
    private String uploadTime;
}
