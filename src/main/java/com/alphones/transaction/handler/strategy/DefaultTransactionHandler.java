package com.alphones.transaction.handler.strategy;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.alphones.transaction.entity.TransactionInfo;
import com.alphones.transaction.handler.AbstractTransactionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Slf4j
@Component
public class DefaultTransactionHandler extends AbstractTransactionHandler {

    @Override
    public void parseTransactionInfo(TransactionReceipt transaction, TransactionInfo transactionInfo) {
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
}
