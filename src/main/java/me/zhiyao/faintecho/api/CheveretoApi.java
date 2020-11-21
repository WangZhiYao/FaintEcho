package me.zhiyao.faintecho.api;

import me.zhiyao.faintecho.api.model.chevereto.UploadResult;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.concurrent.CompletableFuture;

/**
 * @author WangZhiYao
 * @date 2020/11/21
 */
public interface CheveretoApi {

    @GET("api/1/upload/")
    CompletableFuture<UploadResult> upload(@Query(value = "source", encoded = true) String sourceUrl,
                                           @Query("format") String format);
}
