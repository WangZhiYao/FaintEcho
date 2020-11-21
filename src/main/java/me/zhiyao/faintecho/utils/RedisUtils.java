package me.zhiyao.faintecho.utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author WangZhiYao
 * @date 2020/11/21
 */
@Slf4j
@Component
@AllArgsConstructor
public class RedisUtils {

    private final JedisPool jedisPool;

    /**
     * 获取Object
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getObject(String key, Class<T> clazz) {
        try (Jedis jedis = jedisPool.getResource()) {
            String str = jedis.get(key);
            if (!StringUtils.isBlank(str)) {
                return JsonUtils.fromJson(str, clazz);
            }
        } catch (Exception ex) {
            log.error("get: " + key + " failed.", ex);
        }

        return null;
    }

    /**
     * 添加object
     *
     * @param key
     * @param obj
     * @return
     */
    public boolean setObject(String key, Object obj) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, JsonUtils.toJson(obj, false));
            return true;
        } catch (Exception ex) {
            log.error("set: " + key + " failed.", ex);
        }

        return false;
    }

    /**
     * 设置key的过期时间
     *
     * @param key
     * @param seconds
     * @return
     */
    public boolean expire(String key, int seconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.expire(key, seconds) > 0;
        } catch (Exception ex) {
            log.error("set: " + key + " expire failed.", ex);
        }

        return false;
    }


    /**
     * 判断key是否存在
     *
     * @param key
     * @return
     */
    public boolean exists(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key);
        } catch (Exception ex) {
            log.error("exists: " + key + " failed.", ex);
        }

        return false;
    }

    /**
     * 删除指定Key
     *
     * @param key
     * @return
     */
    public boolean delete(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            long ret = jedis.del(key);
            return ret > 0;
        } catch (Exception ex) {
            log.error("delete: " + key + "failed.", ex);
        }
        return false;
    }
}
