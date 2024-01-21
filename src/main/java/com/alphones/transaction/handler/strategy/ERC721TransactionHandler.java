package com.alphones.transaction.handler.strategy;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.alphones.transaction.entity.TransactionInfo;
import com.alphones.transaction.handler.AbstractTransactionHandler;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

@Component
public class ERC721TransactionHandler extends AbstractTransactionHandler {
    public String transferEventSignature() {
        return "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";
    }

    @Override
    public void parseTransactionInfo(final TransactionReceipt transaction, final TransactionInfo transactionInfo) {
        transactionInfo.setStatus(transaction.getStatus());
        transactionInfo.setContractAddress(transaction.getContractAddress());
        transactionInfo.setGas(transaction.getGasUsed());
        transactionInfo.setBlockHash(transaction.getBlockHash());
        transactionInfo.setBlockNumber(transaction.getBlockNumber());
        if (CollectionUtil.isNotEmpty(transaction.getLogs())) {
            transactionInfo.setLog(JSON.toJSONString(transaction.getLogs()));
        }
        final List<Log> logs = transaction.getLogs();
        for (final Log log : logs) {
            final String eventSignature = log.getTopics().get(0);
            if (this.transferEventSignature().equals(eventSignature)) {
                transactionInfo.setFrom(log.getTopics().get(1));
                transactionInfo.setTo(log.getTopics().get(2));
                transactionInfo.setData(new BigInteger(log.getData()));
                break;
            }
        }
    }
}
