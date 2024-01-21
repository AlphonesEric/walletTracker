package com.alphones.token.vo;

import com.alphones.transaction.entity.TransactionInfo;
import com.alphones.wallet.entity.Wallet;
import lombok.Data;

/*
 * use to associate wallet and transaction
 * @author Alphones
 * @date 2024/01/16 19:32
 */
@Data
public class TransactionVo {
    private TransactionInfo transactionInfo;
    private Wallet wallet;
}
