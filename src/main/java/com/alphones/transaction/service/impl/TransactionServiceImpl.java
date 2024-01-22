package com.alphones.transaction.service.impl;

import com.alphones.redis.RedisCache;
import com.alphones.transaction.entity.TransactionInfo;
import com.alphones.transaction.service.ITransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TransactionServiceImpl implements ITransactionService {
    private final String strategyMapKey = "strategyMapKey";
    private final String transactionKey = "transaction";
    @Autowired
    private RedisCache redisCache;

    @Override
    public void addStrategyTypeMap(String transactionType, String className) {
        Map<String, Object> strategyTypeMap = this.redisCache.getCacheMap(strategyMapKey);
        if (strategyTypeMap == null) {
            strategyTypeMap = new HashMap<>(1);
        }
        strategyTypeMap.put(transactionType, className);
        this.redisCache.setCacheMap(strategyMapKey, strategyTypeMap);
    }

    @Override
    public Map<String, String> strategyTypeMap() {
        return this.redisCache.getCacheMap(strategyMapKey);
    }

    @Override
    public boolean saveOrUpdate(TransactionInfo transactionInfo) {
        Map<String, TransactionInfo> cacheMap = this.redisCache.getCacheMap(transactionKey);
        if (cacheMap == null) {
            cacheMap = new HashMap<>(1);
        }
        cacheMap.put(transactionInfo.getHash(), transactionInfo);
        this.redisCache.setCacheMap(transactionKey, cacheMap);
        return true;
    }

    @Override
    public boolean deleteTransaction(String tHx) {
        this.redisCache.deleteCacheMapValue(transactionKey, tHx);
        return true;
    }

    @Override
    public TransactionInfo queryTransaction(String tHx) {
        Map<String, TransactionInfo> cacheMap = this.redisCache.getCacheMap(transactionKey);
        if (cacheMap == null || cacheMap.get(tHx) == null) {
            return null;
        }
        return cacheMap.get(tHx);
    }

    @Override
    public List<TransactionInfo> queryWaitConfirmTransactions() {
        Map<String, TransactionInfo> cacheMap = this.redisCache.getCacheMap(transactionKey);
        if (cacheMap == null) {
            return Collections.emptyList();
        }
        return cacheMap.values().stream().filter(t -> t.getStatus() == null).collect(Collectors.toList());
    }

}
