package com.alphones.transaction.listen;

import com.lmax.disruptor.*;

public class TransactionEventFactory implements EventFactory<TransactionEvent> {
    public TransactionEvent newInstance() {
        return new TransactionEvent();
    }
}
