package com.alphones.transaction.listen;

import com.alphones.transaction.entity.TransactionInfo;
import com.lmax.disruptor.RingBuffer;

public class TransactionProducer {
    private final RingBuffer<TransactionEvent> ringBuffer;

    public TransactionProducer(RingBuffer<TransactionEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(TransactionInfo transactionInfo) {
        long sequence = this.ringBuffer.next();
        try {
            TransactionEvent event = this.ringBuffer.get(sequence);
            event.setTransactionInfo(transactionInfo);
        } finally {
            this.ringBuffer.publish(sequence);
        }
    }
}
