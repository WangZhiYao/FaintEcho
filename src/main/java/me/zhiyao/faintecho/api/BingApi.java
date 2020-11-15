package me.zhiyao.faintecho.api;

import me.zhiyao.faintecho.api.model.bing.HPImageArchive;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.concurrent.CompletableFuture;

/**
 * @author WangZhiYao
 * @date 2020/11/14
 */
public interface BingApi {

    /**
     * 获取 bing 每日图片
     *
     * @param format 返回格式，取值：js代表json，xml
     * @param idx    从startDate 往回推n天，取值范围[0,7]
     * @param n      返回数据条数，取值范围[1,8]
     * @param nc     当前时间戳
     * @param pid    暂不清楚
     * @return
     */
    @GET("HPImageArchive.aspx")
    CompletableFuture<HPImageArchive> getHPImageArchive(@Query("format") String format,
                                                        @Query("idx") int idx,
                                                        @Query("n") int n,
                                                        @Query("nc") long nc,
                                                        @Query("pid") String pid);

    @GET("{imageUrl}")
    CompletableFuture<ResponseBody> download(@Path("imageUrl") String url);
}
