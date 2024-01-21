package com.alphones.token.service.impl;

import com.alphones.redis.RedisCache;
import com.alphones.token.entity.Token;
import com.alphones.token.service.ITokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/*
 * provide user to manage their token config list
 * @author Alphones
 * @date 2024/01/16 19:23
 */
@Slf4j
@Service
public class TokenServiceImpl implements ITokenService {
    @Autowired
    private RedisCache redisCache;
    private final String tokenKey = "token";

    @Override
    public boolean saveOrUpdate(Token token) {
        Map<String, Token> cacheMap = this.redisCache.getCacheMap(tokenKey);
        if (cacheMap == null) {
            cacheMap = new HashMap<>(1);
        }
        cacheMap.put(token.getAddress(), token);
        this.redisCache.setCacheMap(tokenKey, cacheMap);
        return true;
    }

    @Override
    public boolean deleteToken(String address) {
        this.redisCache.deleteCacheMapValue(tokenKey, address);
        return true;
    }

    @Override
    public List<Token> queryTokenList() {
        Map<String, Token> cacheMap = this.redisCache.getCacheMap(tokenKey);
        if (cacheMap == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(cacheMap.values());
    }

    @Override
    public Token queryToken(String address) {
        Map<String, Token> cacheMap = this.redisCache.getCacheMap(tokenKey);
        if (cacheMap == null || cacheMap.get(address) == null) {
            return null;
        }
        return cacheMap.get(address);
    }
}
