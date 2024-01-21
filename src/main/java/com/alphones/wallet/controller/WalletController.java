package com.alphones.wallet.controller;

import com.alphones.token.vo.TransactionVo;
import com.alphones.wallet.entity.Wallet;
import com.alphones.wallet.service.IWalletService;
import com.alphones.wallet.vo.WalletVo;
import com.alphones.web.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/wallet"})
public class WalletController {
    @Autowired
    private IWalletService walletService;

    @GetMapping({"/list/{account}"})
    public R<List<Wallet>> queryWalletList(@PathVariable String account) {
        return R.ok(this.walletService.queryWalletByAccount(account));
    }

    @PostMapping({"/refresh"})
    public R<Wallet> refresh(@RequestBody Wallet wallet) {
        return R.ok(this.walletService.refreshWallet(wallet));
    }

    @PostMapping({"/add"})
    public R<Wallet> addWallet(@RequestBody Wallet wallet) {
        this.walletService.saveOrUpdate(wallet);
        return R.ok(this.walletService.queryWallet(wallet.getAddress()));
    }

    @PostMapping({"/add/token"})
    public R<Wallet> addToken(@RequestBody WalletVo walletVo) {
        this.walletService.saveOrUpdateWalletToken(walletVo.getWallet(), walletVo.getToken());
        return R.ok(this.walletService.queryWallet(walletVo.getWallet().getAddress()));
    }

    @PostMapping({"/add/transaction"})
    public R<Wallet> addTransaction(@RequestBody TransactionVo transactionVo) {
        this.walletService.addTransaction(transactionVo);
        return R.ok(this.walletService.queryWallet(transactionVo.getWallet().getAddress()));
    }
}
