package com.alphones.transaction.listen;

import com.alphones.transaction.entity.TransactionInfo;

public class TransactionEvent {
    private TransactionInfo transactionInfo;

    public TransactionInfo getTransactionInfo() {
        return this.transactionInfo;
    }

    public void setTransactionInfo(final TransactionInfo transactionInfo) {
        this.transactionInfo = transactionInfo;
    }

}
