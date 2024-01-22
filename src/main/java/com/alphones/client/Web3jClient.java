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
    private static Map<BigInteger, String> chainId2ChainNet;

    public static Web3j getClient(String node) {
        return Web3j.build(new HttpService(node));
    }

    public static Web3j getClient(BigInteger chainId) {
        String node = Web3jClient.chainId2ChainNet.get(chainId);
        return Web3j.build(new HttpService(node));
    }

    public static BigInteger getClientChainId(Web3j web3j) throws IOException {
        NetVersion netVersion = web3j.netVersion().send();
        return new BigInteger(netVersion.getNetVersion());
    }

    public static BigDecimal getUserChainBalance(Web3j web3j, String address) throws IOException {
        BigInteger balanceWei = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
        return Convert.fromWei(balanceWei.toString(), Convert.Unit.ETHER);
    }

    public static void getEthInfo(Web3j web3j) {
        Web3ClientVersion web3ClientVersion = null;
        try {
            web3ClientVersion = web3j.web3ClientVersion().send();
            String clientVersion = web3ClientVersion.getWeb3ClientVersion();
            System.out.println("clientVersion " + clientVersion);
            EthBlockNumber ethBlockNumber = web3j.ethBlockNumber().send();
            BigInteger blockNumber = ethBlockNumber.getBlockNumber();
            System.out.println(blockNumber);
            EthCoinbase ethCoinbase = web3j.ethCoinbase().send();
            String coinbaseAddress = ethCoinbase.getAddress();
            System.out.println(coinbaseAddress);
            EthSyncing ethSyncing = web3j.ethSyncing().send();
            boolean isSyncing = ethSyncing.isSyncing();
            System.out.println(isSyncing);
            EthMining ethMining = web3j.ethMining().send();
            boolean isMining = ethMining.isMining();
            System.out.println(isMining);
            EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
            BigInteger gasPrice = ethGasPrice.getGasPrice();
            System.out.println(gasPrice);
            EthHashrate ethHashrate = web3j.ethHashrate().send();
            BigInteger hashRate = ethHashrate.getHashrate();
            System.out.println(hashRate);
            EthProtocolVersion ethProtocolVersion = web3j.ethProtocolVersion().send();
            String protocolVersion = ethProtocolVersion.getProtocolVersion();
            System.out.println(protocolVersion);
            NetPeerCount netPeerCount = web3j.netPeerCount().send();
            BigInteger peerCount = netPeerCount.getQuantity();
            System.out.println(peerCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        (chainId2ChainNet = new HashMap<BigInteger, String>()).put(new BigInteger("11155111"), "https://sepolia.infura.io/v3/6342964c4b11438d84d80f6983218ce2");
    }
}
