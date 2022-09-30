package com.example.demo.config.redis;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.time.Duration;

/**
 * @author ljr
 * @date 2022-08-29 10:24
 */
public class CustomizerRedisCacheManager extends RedisCacheManager {

    /**
     * 过期时间分隔符，可以用其他符号
     */
    private final char SEPARATOR = '#';

    public CustomizerRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
    }

    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        int pos = name.lastIndexOf(SEPARATOR);
        if(pos > 0){
            try{
                Long t = Long.parseLong(name.substring(pos + 1));
                name = name.substring(0, pos);
                cacheConfig = cacheConfig.entryTtl(Duration.ofSeconds(t));
            }
            catch (Exception e){
                //不作处理
            }
        }
        return super.createRedisCache(name, cacheConfig);
    }
}
