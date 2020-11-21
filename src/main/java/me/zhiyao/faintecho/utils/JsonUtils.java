package me.zhiyao.faintecho.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author WangZhiYao
 * @date 2020/10/23
 */
public class JsonUtils {

    public static <T> String toJson(T obj, boolean pretty) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        if (pretty) {
            gsonBuilder.setPrettyPrinting();
        }

        Gson gson = gsonBuilder.create();
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String str, Class<T> clz) {
        return new Gson().fromJson(str, clz);
    }
}
