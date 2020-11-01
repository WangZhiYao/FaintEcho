package me.zhiyao.faintecho.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author WangZhiYao
 * @date 2020/10/23
 */
public class JsonUtils {

    public static String toJson(Object obj) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(obj);
    }
}
