package me.zhiyao.faintecho.api.model.bing;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @author WangZhiYao
 * @date 2020/11/15
 */
@Data
public class Image {

    @SerializedName("startdate")
    private String startDate;
    @SerializedName("fullstartdate")
    private String fullStartDate;
    @SerializedName("enddate")
    private String endDate;
    private String url;
    @SerializedName("urlbase")
    private String urlBase;
    private String copyright;
    @SerializedName("copyrightlink")
    private String copyrightLink;
    private String title;
    private String quiz;
    private boolean wp;
    private String hsh;
    private int drk;
    private int top;
    private int bot;

}
