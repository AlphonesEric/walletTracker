package com.alphones.transaction;

import com.alphones.client.Web3jClient;
import com.alphones.transaction.entity.TransactionInfo;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigInteger;

@Slf4j
public class TransactionChainService {
    private static Disposable subscribe;

    public static BigInteger queryAccountNonce(Web3j web3j, String accountAddress) throws Exception {
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(accountAddress, DefaultBlockParameterName.LATEST).send();
        return ethGetTransactionCount.getTransactionCount();
    }

    public static EthGasPrice queryChainTractionGasPrice(Web3j web3j) throws IOException {
        return web3j.ethGasPrice().send();
    }

    public static TransactionInfo queryTransactionInfoFromChain(Web3j web3j, String tHx) {
        TransactionInfo transactionInfo = null;
        try {
            BigInteger chainId = Web3jClient.getClientChainId(web3j);
            EthTransaction ethTransaction = web3j.ethGetTransactionByHash(tHx).send();
            if (ethTransaction.getTransaction().isPresent()) {
                Transaction transaction = ethTransaction.getTransaction().get();
                transactionInfo = new TransactionInfo();
                transactionInfo.setHash(transaction.getHash());
                transactionInfo.setNonce(transaction.getNonce());
                transactionInfo.setFrom(transaction.getFrom());
                transactionInfo.setTo(transaction.getTo());
                transactionInfo.setGas(transaction.getGas());
                transactionInfo.setBlockHash(transaction.getBlockHash());
                transactionInfo.setBlockNumber(transaction.getBlockNumber());
                transactionInfo.setChainId(chainId);
            }
        } catch (IOException e) {
            TransactionChainService.log.error("query transaction from chain error,txHash:{},error info:{}", tHx, e.getMessage());
        }
        return transactionInfo;
    }

    public static boolean checkTransactionIsCompleted(Web3j web3j, String transactionTx) {
        try {
            EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(transactionTx).send();
            if (receipt.getTransactionReceipt().isPresent()) {
                return receipt.getTransactionReceipt().get().getBlockHash() != null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static synchronized void subscriptLatestBlock(Web3j web3j) {
        try {
            if (TransactionChainService.subscribe != null) {
                return;
            }
            TransactionChainService.subscribe = web3j.blockFlowable(false).subscribe(block -> {
                EthBlock.Block curBlock = block.getBlock();
                System.out.println("blockHash:" + curBlock.getHash());
                for (EthBlock.TransactionResult<?> tx : curBlock.getTransactions()) {
                    EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(tx.get().toString()).send();
                    receipt.getTransactionReceipt().ifPresent(transaction -> {
                        System.out.println("transactionHash:" + transaction.getTransactionHash());
                        System.out.println("contractAddress:" + transaction.getContractAddress());
                        System.out.println("from:" + transaction.getFrom());
                        System.out.println("to:" + transaction.getTo());
                        System.out.println("gasPrice:" + Convert.fromWei(transaction.getGasUsed().toString(), Convert.Unit.GWEI));
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized boolean stopSubscriptBlock() {
        if (TransactionChainService.subscribe != null) {
            TransactionChainService.subscribe.dispose();
            return TransactionChainService.subscribe.isDisposed();
        }
        return true;
    }

}
