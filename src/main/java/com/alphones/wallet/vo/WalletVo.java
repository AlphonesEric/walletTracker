package com.alphones.wallet.vo;

import com.alphones.token.entity.Token;
import com.alphones.wallet.entity.Wallet;

/**
 * ues to associate
 * @author Alphones
 * @date 2024/01/16 19:41
 */
public class WalletVo {
    private Wallet wallet;
    private Token token;

    public Wallet getWallet() {
        return this.wallet;
    }

    public Token getToken() {
        return this.token;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
