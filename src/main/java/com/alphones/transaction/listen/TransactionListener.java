package com.alphones.transaction.listen;

import com.alphones.transaction.entity.TransactionInfo;
import com.alphones.transaction.handler.TransactionHandleService;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

@Component
public class TransactionListener {
    private int bufferSize = 1024;
    private int consumerSize = 5;
    @Autowired
    private TransactionHandleService handleService;
    private TransactionProducer producer;

    @PostConstruct
    public void transactionListen() {
        TransactionEventFactory factory = new TransactionEventFactory();
        Disruptor<TransactionEvent> disruptor = new Disruptor<>(factory, bufferSize, Executors.defaultThreadFactory(), ProducerType.SINGLE, (WaitStrategy) new BlockingWaitStrategy());
        List<TransactionConsumer> consumers = new ArrayList<>(consumerSize);
        for (int i = 0; i < 5; ++i) {
            consumers.add(new TransactionConsumer(this.handleService, this));
        }
        disruptor.handleEventsWith(consumers.toArray(new TransactionConsumer[]{}));
        disruptor.start();
        RingBuffer<TransactionEvent> ringBuffer = disruptor.getRingBuffer();
        this.producer = new TransactionProducer(ringBuffer);
    }

    public void putTransaction(TransactionInfo transactionInfo) {
        if (this.producer != null) {
            this.producer.onData(transactionInfo);
        }
    }
}
