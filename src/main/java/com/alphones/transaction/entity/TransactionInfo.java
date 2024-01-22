package com.alphones.transaction.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class TransactionInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String type;
    private String hash;
    private BigInteger chainId;
    private BigInteger data;
    private String log;
    private LocalDateTime transactionTime;
    private String status;
    private String from;
    private String to;
    private String contractAddress;
    private BigInteger gas;
    private BigInteger blockNumber;
    private String blockHash;
    private BigInteger nonce;
    private Integer reTryCount = 3;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionInfo)) {
            return false;
        }
        TransactionInfo t = (TransactionInfo) o;
        return this.getHash().equals(t.getHash());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.hash);
    }

}
