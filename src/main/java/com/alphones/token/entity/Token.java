package com.alphones.token.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

/*
 * token entity
 * @author Alphones
 * @date 2024/01/16 19:31
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Token implements Serializable {
    private static final long serialVersionUID = 1L;
    private String address;
    private String name;
    private String symbol;
    private Integer tokenDecimals;
    private BigInteger totalSupply;
    private BigInteger chainId;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Token)) {
            return false;
        }
        Token other = (Token)o;
        return this.getAddress().equals(other.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.address);
    }


}
