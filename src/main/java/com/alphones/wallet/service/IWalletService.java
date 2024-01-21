package com.alphones.wallet.service;

import com.alphones.token.entity.Token;
import com.alphones.token.vo.TransactionVo;
import com.alphones.wallet.entity.Wallet;

import java.util.List;

/**
 *  wallet service, which provide user to manage their wallet
 * @author Alphones
 * @date 2024/01/16 16:48
 */
public interface IWalletService {
    List<Wallet> queryWalletByAccount(String account);

    Wallet queryWallet(String account);

    boolean saveOrUpdate(Wallet wallet);

    boolean saveOrUpdateWalletToken(Wallet wallet, Token token);

    Wallet refreshWallet(Wallet wallet);

    boolean addTransaction(TransactionVo transactionVo);
}
