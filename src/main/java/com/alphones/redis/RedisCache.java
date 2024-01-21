package com.alphones.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * spring redis client
 *
 * @author admin
 **/
@Slf4j
@SuppressWarnings(value = {"unchecked", "rawtypes"})
@Component
public class RedisCache {

    @Autowired
    public RedisTemplate redisTemplate;

    /**
     * cache base type data
     *
     * @param key   redis key
     * @param value  redis value
     */
    public <T> void setCacheObject(String key, T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * cache Object
     *
     * @param key
     * @param value
     * @param timeout
     * @param timeUnit
     */
    public <T> void setCacheObject(String key, T value, Integer timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * set cache expire
     *
     * @param key
     * @param timeout
     * @return true-success；false=fail
     */
    public boolean expire(String key, long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * set cache expire
     *
     * @param key
     * @param timeout
     * @param unit
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * get cache key expire
     *
     * @param key Redis键
     * @return 有效时间
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * exist key
     *
     * @param key
     * @return true exist , false not exist
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * get cache object
     *
     * @param key
     * @return
     */
    public <T> T getCacheObject(String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * delete cache by key
     *
     * @param key
     */
    public Boolean deleteObject(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * delete by key collection
     *
     * @param keyCollection key collection
     */
    public boolean deleteObject(Collection keyCollection) {
        Long deleteCount = redisTemplate.delete(keyCollection);
        return deleteCount != null && deleteCount > 0;
    }

    /**
     * cache list
     *
     * @param key
     * @param dataList
     * @return cache number
     */
    public <T> long setCacheList(String key, List<T> dataList) {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * get cache list
     *
     * @param key
     * @return
     */
    public <T> List<T> getCacheList(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * cache set
     *
     * @param key
     * @param dataSet
     * @return cache set data
     */
    public <T> BoundSetOperations<String, T> setCacheSet(String key, Set<T> dataSet) {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext()) {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    /**
     * get cache set
     *
     * @param key
     */
    public <T> Set<T> getCacheSet(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * cache map
     *
     * @param key
     * @param dataMap
     */
    public <T> void setCacheMap(String key, Map<String, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * get cache map
     *
     * @param key
     */
    public <T> Map<String, T> getCacheMap(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * cache hash value
     *
     * @param key   Redis key
     * @param hKey  Hash key
     * @param value Hash value
     */
    public <T> void setCacheMapValue(String key, String hKey, T value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * get cache hash value
     *
     * @param key
     * @param hKey
     */
    public <T> T getCacheMapValue(String key, String hKey) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * get hash cache multi-value
     *
     * @param key
     * @param hKeys
     */
    public <T> List<T> getMultiCacheMapValue(String key, Collection<Object> hKeys) {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * delete hash cache value
     * @param key
     * @param hKey
     * @return is deleted success
     */
    public boolean deleteCacheMapValue(String key, String hKey) {
        return redisTemplate.opsForHash().delete(key, hKey) > 0;
    }

    /**
     * get cache collection by patter key
     *
     * @param pattern
     */
    public Collection<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }


}
