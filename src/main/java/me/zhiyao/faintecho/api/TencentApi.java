package me.zhiyao.faintecho.api;

import me.zhiyao.faintecho.api.model.tencent.TextChat;
import me.zhiyao.faintecho.api.response.BaseResponse;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import java.util.concurrent.CompletableFuture;

/**
 * @author WangZhiYao
 * @date 2020/11/23
 */
public interface TencentApi {

    @FormUrlEncoded
    @POST("fcgi-bin/nlp/nlp_textchat")
    CompletableFuture<BaseResponse<TextChat>> textChat(@Field("session") String session,
                                                       @Field("question") String question);
}
