package com.alphones.wallet.entity;

import com.alphones.token.entity.Token;
import com.alphones.transaction.entity.TransactionInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
 * wallet entity
 * @author Alphones
 * @date 2024/01/16 16:55
 */
@Data
public class Wallet implements Serializable {
    private static final long serialVersionUID = 1L;
    private String account;
    private String walletName;
    private String address;
    private BigDecimal gasBalance;
    private BigInteger chainId;
    private String chainNet;
    @JsonIgnore
    private Map<Token, BigInteger> tokens;
    private Map<String, BigInteger> tokenBalance;
    private Map<String, Token> tokenMap;
    private Set<TransactionInfo> transactionInfos;

    public Map<String, BigInteger> getTokenBalance() {
        return this.tokens.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getAddress(), Map.Entry::getValue));
    }

    public Map<String, Token> getTokenMap() {
        return this.tokens.keySet().stream().collect(Collectors.toMap(Token::getAddress, Function.identity()));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Wallet)) {
            return false;
        }
        Wallet t = (Wallet) o;
        return this.getAddress().equals(t.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.address);
    }

    public Wallet() {
        this.tokens = new HashMap<>();
        this.transactionInfos = new LinkedHashSet<>();
    }

}
