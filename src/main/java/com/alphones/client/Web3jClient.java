package com.alphones.client;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/*
 * client witch interact with ethereum net about client info
 * @author Alphones
 * @date 2024/01/16 19:04
 */
public class Web3jClient {
    private static final Map<BigInteger, String> chainId2ChainNet;

    public static Web3j getClient(final String node) {
        return Web3j.build(new HttpService(node));
    }

    public static Web3j getClient(final BigInteger chainId) {
        final String node = Web3jClient.chainId2ChainNet.get(chainId);
        return Web3j.build(new HttpService(node));
    }

    public static BigInteger getClientChainId(final Web3j web3j) throws IOException {
        final NetVersion netVersion = web3j.netVersion().send();
        return new BigInteger(netVersion.getNetVersion());
    }

    public static BigDecimal getUserChainBalance(final Web3j web3j, final String address) throws IOException {
        final BigInteger balanceWei = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
        return Convert.fromWei(balanceWei.toString(), Convert.Unit.ETHER);
    }

    public static void getEthInfo(final Web3j web3j) {
        Web3ClientVersion web3ClientVersion = null;
        try {
            web3ClientVersion = web3j.web3ClientVersion().send();
            final String clientVersion = web3ClientVersion.getWeb3ClientVersion();
            System.out.println("clientVersion " + clientVersion);
            final EthBlockNumber ethBlockNumber = web3j.ethBlockNumber().send();
            final BigInteger blockNumber = ethBlockNumber.getBlockNumber();
            System.out.println(blockNumber);
            final EthCoinbase ethCoinbase = web3j.ethCoinbase().send();
            final String coinbaseAddress = ethCoinbase.getAddress();
            System.out.println(coinbaseAddress);
            final EthSyncing ethSyncing = web3j.ethSyncing().send();
            final boolean isSyncing = ethSyncing.isSyncing();
            System.out.println(isSyncing);
            final EthMining ethMining = web3j.ethMining().send();
            final boolean isMining = ethMining.isMining();
            System.out.println(isMining);
            final EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
            final BigInteger gasPrice = ethGasPrice.getGasPrice();
            System.out.println(gasPrice);
            final EthHashrate ethHashrate = web3j.ethHashrate().send();
            final BigInteger hashRate = ethHashrate.getHashrate();
            System.out.println(hashRate);
            final EthProtocolVersion ethProtocolVersion = web3j.ethProtocolVersion().send();
            final String protocolVersion = ethProtocolVersion.getProtocolVersion();
            System.out.println(protocolVersion);
            final NetPeerCount netPeerCount = web3j.netPeerCount().send();
            final BigInteger peerCount = netPeerCount.getQuantity();
            System.out.println(peerCount);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    static {
        (chainId2ChainNet = new HashMap<BigInteger, String>()).put(new BigInteger("11155111"), "https://sepolia.infura.io/v3/6342964c4b11438d84d80f6983218ce2");
    }
}
