package com.alphones.transaction.handler;

import com.alphones.client.Web3jClient;
import com.alphones.transaction.entity.TransactionInfo;
import com.alphones.transaction.service.ITransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractTransactionHandler {
    @Autowired
    protected ITransactionService transactionService;
    private Map<BigInteger, Web3j> chain2Client;

    public AbstractTransactionHandler() {
        this.chain2Client = new HashMap<>();
    }

    public synchronized Web3j getClient(BigInteger chainId) {
        Web3j client = null;
        try {
            if ((client = this.chain2Client.get(chainId)) == null) {
                client = Web3jClient.getClient(chainId);
                this.chain2Client.put(chainId, client);
            }
        } catch (Exception e) {
            AbstractTransactionHandler.log.error("chainId '{}' link error", chainId);
        }
        return client;
    }

    public boolean handle(TransactionInfo transactionInfo) {
        Web3j web3j = this.getClient(transactionInfo.getChainId());
        if (web3j == null) {
            transactionInfo.setReTryCount(0);
            transactionInfo.setLog("chain net link error");
            return false;
        }
        try {
            EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(transactionInfo.getHash()).send();
            if (receipt.getTransactionReceipt().isPresent()) {
                TransactionReceipt transaction = receipt.getTransactionReceipt().get();
                this.parseTransactionInfo(transaction, transactionInfo);
                this.transactionService.saveOrUpdate(transactionInfo);
                return true;
            }
        } catch (IOException e) {
            AbstractTransactionHandler.log.error("get transaction in chain error,transaction hash:{},error info:{}", transactionInfo.getHash(), e.getMessage());
        }
        return false;
    }

    public abstract void parseTransactionInfo(TransactionReceipt transactionReceipt, TransactionInfo transactionInfo);

}
