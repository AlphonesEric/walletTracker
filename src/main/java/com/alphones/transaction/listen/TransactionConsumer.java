package com.alphones.transaction.listen;

import cn.hutool.core.util.StrUtil;
import com.alphones.transaction.entity.TransactionInfo;
import com.alphones.transaction.handler.TransactionHandleService;
import com.lmax.disruptor.*;

public class TransactionConsumer implements EventHandler<TransactionEvent> {
    private TransactionHandleService transactionService;
    private TransactionListener listener;

    public void onEvent(TransactionEvent transactionEvent, long l, boolean b) throws Exception {
        TransactionInfo transactionInfo = transactionEvent.getTransactionInfo();
        if (StrUtil.isBlank(transactionInfo.getHash()) || transactionInfo.getChainId() == null) {
            return;
        }
        boolean handle = this.transactionService.handle(transactionInfo);
        Integer reTryCount = transactionInfo.getReTryCount();
        if (!handle && reTryCount > 0) {
            transactionInfo.setReTryCount(reTryCount - 1);
            this.listener.putTransaction(transactionInfo);
        }
    }

    public TransactionConsumer(TransactionHandleService transactionService, TransactionListener listener) {
        this.transactionService = transactionService;
        this.listener = listener;
    }
}
