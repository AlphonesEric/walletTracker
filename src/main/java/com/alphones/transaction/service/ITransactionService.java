package com.alphones.transaction.service;

import com.alphones.transaction.entity.TransactionInfo;

import java.util.List;
import java.util.Map;

/*
 * @author Alphones
 * @date 2024/01/16 19:36
 */
public interface ITransactionService {
    /*
     * add strategy about handling different transaction
     * @see com.alphones.transaction.handler.AbstractTransactionHandler
     * @author Alphones
     * @date 2024/01/16 19:35
     * @param transactionType
     * @param className
     */
    void addStrategyTypeMap(String transactionType, String className);

    /*
     * query current strategy map
     * @author Alphones
     * @date 2024/01/16 19:37
     * @return java.util.Map<java.lang.String,java.lang.String>
     */
    Map<String, String> strategyTypeMap();

    /*
     * save or update a transaction
     * @author Alphones
     * @date 2024/01/16 19:38
     * @param transactionInfo
     * @return boolean
     */
    boolean saveOrUpdate(TransactionInfo transactionInfo);

    /*
     * delete a transaction by hash
     * @author Alphones
     * @date 2024/01/16 19:38
     * @param tHx
     * @return boolean
     */
    boolean deleteTransaction(String tHx);

    /*
     * query
     * @author Alphones
     * @date 2024/01/16 19:38
     * @param tHx
     * @return com.alphones.transaction.entity.TransactionInfo
     */
    TransactionInfo queryTransaction(String tHx);

    List<TransactionInfo> queryWaitConfirmTransactions();
}
