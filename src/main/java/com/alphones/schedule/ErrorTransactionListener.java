package com.alphones.schedule;

import com.alphones.transaction.entity.TransactionInfo;
import com.alphones.transaction.listen.TransactionListener;
import com.alphones.transaction.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/*
 * track transaction which has confirmed for a long time
 * @author Alphones
 * @date 2024/01/16 19:15
 */
// @EnableScheduling
// @Component
public class ErrorTransactionListener {

    @Autowired
    private ITransactionService transactionService;
    @Autowired
    private TransactionListener transactionListener;

    @Scheduled(cron = "0 0 0 * * ?")    // every day at 00:00
    public void trackNoConfirmTransaction() {
        List<TransactionInfo> transactionInfos = transactionService.queryWaitConfirmTransactions();
        transactionInfos.forEach(t -> transactionListener.putTransaction(t));
    }
}
