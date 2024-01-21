package com.alphones.transaction.handler.strategy;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.alphones.transaction.entity.TransactionInfo;
import com.alphones.transaction.handler.AbstractTransactionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.Collection;

@Component
public class DefaultTransactionHandler extends AbstractTransactionHandler {
    private static final Logger log;

    @Override
    public void parseTransactionInfo(final TransactionReceipt transaction, final TransactionInfo transactionInfo) {
        transactionInfo.setStatus(transaction.getStatus());
        transactionInfo.setFrom(transaction.getFrom());
        transactionInfo.setTo(transaction.getTo());
        transactionInfo.setGas(transaction.getGasUsed());
        transactionInfo.setBlockHash(transaction.getBlockHash());
        transactionInfo.setBlockNumber(transaction.getBlockNumber());
        if (CollectionUtil.isNotEmpty(transaction.getLogs())) {
            transactionInfo.setLog(JSON.toJSONString(transaction.getLogs()));
        }
    }

    static {
        log = LoggerFactory.getLogger(DefaultTransactionHandler.class);
    }
}
