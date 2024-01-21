package com.alphones.transaction;

import com.alphones.client.Web3jClient;
import com.alphones.transaction.entity.TransactionInfo;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigInteger;

public class TransactionChainService {
    private static final Logger log;
    private static Disposable subscribe;

    public static BigInteger queryAccountNonce(final Web3j web3j, final String accountAddress) throws Exception {
        final EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(accountAddress, DefaultBlockParameterName.LATEST).send();
        return ethGetTransactionCount.getTransactionCount();
    }

    public static EthGasPrice queryChainTractionGasPrice(final Web3j web3j) throws IOException {
        return web3j.ethGasPrice().send();
    }

    public static TransactionInfo queryTransactionInfoFromChain(final Web3j web3j, final String tHx) {
        TransactionInfo transactionInfo = null;
        try {
            final BigInteger chainId = Web3jClient.getClientChainId(web3j);
            final EthTransaction ethTransaction = web3j.ethGetTransactionByHash(tHx).send();
            if (ethTransaction.getTransaction().isPresent()) {
                final Transaction transaction = ethTransaction.getTransaction().get();
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
        } catch (final IOException e) {
            TransactionChainService.log.error("query transaction from chain error,txHash:{},error info:{}", tHx, e.getMessage());
        }
        return transactionInfo;
    }

    public static boolean checkTransactionIsCompleted(final Web3j web3j, final String transactionTx) {
        try {
            final EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(transactionTx).send();
            if (receipt.getTransactionReceipt().isPresent()) {
                return receipt.getTransactionReceipt().get().getBlockHash() != null;
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static synchronized void subscriptLatestBlock(final Web3j web3j) {
        try {
            if (TransactionChainService.subscribe != null) {
                return;
            }
            TransactionChainService.subscribe = web3j.blockFlowable(false).subscribe(block -> {
                final EthBlock.Block curBlock = block.getBlock();
                System.out.println("blockHash:" + curBlock.getHash());
                for (EthBlock.TransactionResult<?> tx : curBlock.getTransactions()) {
                    final EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(tx.get().toString()).send();
                    receipt.getTransactionReceipt().ifPresent(transaction -> {
                        System.out.println("transactionHash:" + transaction.getTransactionHash());
                        System.out.println("contractAddress:" + transaction.getContractAddress());
                        System.out.println("from:" + transaction.getFrom());
                        System.out.println("to:" + transaction.getTo());
                        System.out.println("gasPrice:" + Convert.fromWei(transaction.getGasUsed().toString(), Convert.Unit.GWEI));
                    });
                }
            });
        } catch (final Exception e) {
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

    static {
        log = LoggerFactory.getLogger(TransactionChainService.class);
    }
}
