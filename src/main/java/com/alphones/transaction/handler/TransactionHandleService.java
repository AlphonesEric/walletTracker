package com.alphones.transaction.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.alphones.transaction.entity.TransactionInfo;
import com.alphones.transaction.handler.strategy.DefaultTransactionHandler;
import com.alphones.transaction.service.ITransactionService;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TransactionHandleService {
    @Autowired
    private ITransactionService transactionService;
    private Map<Class<?>, AbstractTransactionHandler> strategyMap;

    public boolean handle(TransactionInfo transactionInfo) {
        AbstractTransactionHandler strategy = this.getStrategy(transactionInfo);
        return strategy.handle(transactionInfo);
    }

    @Autowired
    public TransactionHandleService(List<AbstractTransactionHandler> strategyList) {
        if (!CollectionUtil.isEmpty(strategyList)) {
            this.strategyMap = strategyList.stream().collect(Collectors.toMap((Function<? super Object, ? extends Class<?>>) AopUtils::getTargetClass, Function.identity()));
        } else {
            this.strategyMap = new HashMap<>();
        }
    }

    private AbstractTransactionHandler getStrategy(TransactionInfo transactionInfo) {
        String type = this.transactionService.strategyTypeMap().get(transactionInfo.getType());
        AbstractTransactionHandler strategy = null;
        try {
            if (type == null || (strategy = this.strategyMap.get(Class.forName(type))) == null) {
                strategy = this.strategyMap.get(DefaultTransactionHandler.class);
            }
        } catch (Exception e) {
            strategy = this.strategyMap.get(DefaultTransactionHandler.class);
        }
        return strategy;
    }
}
