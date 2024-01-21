package com.alphones.wallet.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alphones.client.Web3jClient;
import com.alphones.redis.RedisCache;
import com.alphones.token.entity.Token;
import com.alphones.token.vo.TransactionVo;
import com.alphones.transaction.entity.TransactionInfo;
import com.alphones.transaction.listen.TransactionListener;
import com.alphones.transaction.service.ITransactionService;
import com.alphones.wallet.WalletChainService;
import com.alphones.wallet.entity.Wallet;
import com.alphones.wallet.service.IWalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
public class WalletServiceImpl implements IWalletService {

    @Autowired
    private RedisCache redisCache;
    @Autowired
    private ITransactionService transactionService;
    @Autowired
    private TransactionListener transactionListener;
    private final String walletKey = "wallet";

    @Override
    public List<Wallet> queryWalletByAccount(String account) {
        Map<String, Wallet> cacheMap = this.redisCache.getCacheMap(walletKey);
        if (cacheMap == null) {
            return Collections.emptyList();
        }
        return cacheMap.values().stream().filter(w -> account.equals(w.getAccount())).collect(Collectors.toList());
    }

    @Override
    public Wallet queryWallet(String address) {
        Map<String, Wallet> cacheMap = this.redisCache.getCacheMap(walletKey);
        if (cacheMap == null || cacheMap.get(address) == null) {
            return null;
        }
        return cacheMap.get(address);
    }

    @Override
    public boolean saveOrUpdate(Wallet wallet) {
        if (StrUtil.isBlank(wallet.getAccount()) || StrUtil.isBlank(wallet.getAddress()) || StrUtil.isBlank(wallet.getChainNet())) {
            return false;
        }
        Object cacheMapValue = Optional.ofNullable(this.redisCache.getCacheMapValue(walletKey, wallet.getAddress())).orElse(wallet);
        BeanUtils.copyProperties(wallet, cacheMapValue, "tokens", "transactionInfos");
        this.redisCache.setCacheMapValue(walletKey, wallet.getAddress(), cacheMapValue);
        return true;
    }

    @Override
    public boolean saveOrUpdateWalletToken(Wallet wallet, Token token) {
        Web3j web3j = null;
        try {
            web3j = Web3jClient.getClient(wallet.getChainNet());
            BigInteger tokenBalance = WalletChainService.getTokenBalance(web3j, wallet.getAddress(), token.getAddress());
            Wallet cacheMapValue = (Wallet) Optional.ofNullable(this.redisCache.getCacheMapValue(walletKey, wallet.getAddress())).orElse(wallet);
            cacheMapValue.getTokens().put(token, tokenBalance);
            this.redisCache.setCacheMapValue("wallet", wallet.getAddress(), cacheMapValue);
        } catch (Exception e) {
            WalletServiceImpl.log.error("save or update wallet token map error, wallet address:{},token address:{},error:{}", wallet.getAddress(), token.getAddress(), e.getMessage());
            return false;
        } finally {
            if (web3j != null) {
                web3j.shutdown();
            }
        }
        return true;
    }

    @Override
    public boolean addTransaction(TransactionVo transactionVo) {
        TransactionInfo transactionInfo = transactionVo.getTransactionInfo();
        Wallet wallet = transactionVo.getWallet();
        if (this.transactionService.saveOrUpdate(transactionInfo)) {
            this.transactionListener.putTransaction(transactionInfo);
            Wallet cacheMapValue = (Wallet) Optional.ofNullable(this.redisCache.getCacheMapValue("wallet", wallet.getAddress())).orElse(wallet);
            cacheMapValue.getTransactionInfos().add(transactionInfo);
            this.redisCache.setCacheMapValue(walletKey, wallet.getAddress(), cacheMapValue);
            return true;
        }
        return false;
    }

    @Override
    public Wallet refreshWallet(Wallet wallet) {
        if (wallet == null || StrUtil.isBlank(wallet.getAddress())) {
            return wallet;
        }
        Wallet walletCache = this.queryWallet(wallet.getAddress());
        Web3j web3j = null;
        try {
            web3j = Web3jClient.getClient(walletCache.getChainNet());
            for (Map.Entry<Token, BigInteger> map : walletCache.getTokens().entrySet()) {
                Token token = map.getKey();
                if (!token.getChainId().equals(walletCache.getChainId())) {
                    continue;
                }
                BigInteger tokenBalance = WalletChainService.getTokenBalance(web3j, walletCache.getAddress(), token.getAddress());
                walletCache.getTokens().put(token, tokenBalance);
            }
            walletCache.getTransactionInfos().forEach(t -> BeanUtils.copyProperties(this.transactionService.queryTransaction(t.getHash()), t));
            this.redisCache.setCacheMapValue(walletKey, wallet.getAddress(), walletCache);
        } catch (Exception e) {
            WalletServiceImpl.log.error("refresh wallet  error, wallet address:{},chainId :{},error:{}", walletCache.getAddress(), walletCache.getChainId(), e.getMessage());
        } finally {
            if (web3j != null) {
                web3j.shutdown();
            }
        }
        return walletCache;
    }
}
