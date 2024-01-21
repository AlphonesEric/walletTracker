package com.alphones.token;

import cn.hutool.core.collection.CollectionUtil;
import com.alphones.client.Web3jClient;
import com.alphones.config.Config;
import com.alphones.token.entity.Token;
import lombok.extern.slf4j.Slf4j;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.http.HttpService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/*
 * interact ethereum about tokens(base on erc20)
 * @author Alphones
 * @date 2024/01/16 19:34
 */
@Slf4j
public class TokenChainService {

    /*
     * query token info(base erc20)
     * @author Alphones
     * @date 2024/01/18 18:06
     * @param address
     * @param chain
     * @return com.alphones.token.entity.Token
     */
    public static Token queryTokenInfoFromChain(String address, String chain) {
        Web3j web3j = null;
        try {
            web3j = Web3j.build(new HttpService(chain));
            String tokenName = getTokenName(web3j, address);
            String tokenSymbol = getTokenSymbol(web3j, address);
            Integer tokenDecimals = getTokenDecimals(web3j, address);
            BigInteger tokenTotalSupply = getTokenTotalSupply(web3j, address);
            BigInteger chainId = Web3jClient.getClientChainId(web3j);
            return new Token(address, tokenName, tokenSymbol, tokenDecimals, tokenTotalSupply, chainId);
        } catch (Exception e) {
            TokenChainService.log.warn("query token info from chain error:{}", e.getMessage());
        } finally {
            if (web3j != null) {
                web3j.shutdown();
            }
        }
        return null;
    }


    public static Object ethCallSingleResult(Web3j web3j, String methodName, TypeReference<?> resultType, String contractAddress) {
        Object callInfo = null;
        String fromAddr = Config.emptyAddress;
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();
        outputParameters.add(resultType);
        Function function = new Function(methodName, inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);
        EthCall ethCall = null;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            if (CollectionUtil.isEmpty(results)) {
                TokenChainService.log.error("ethCall error or has no result,method:{},reverted reason:{}", methodName, ethCall.getRevertReason());
                return null;
            }
            callInfo = results.get(0).getValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return callInfo;
    }

    public static String getTokenName(Web3j web3j, String contractAddress) {
        String methodName = "name";
        TypeReference<Utf8String> typeReference = new TypeReference<Utf8String>() {
        };
        Object callResult = ethCallSingleResult(web3j, methodName, typeReference, contractAddress);
        if (callResult != null) {
            return callResult.toString();
        }
        return null;
    }

    public static String getTokenSymbol(Web3j web3j, String contractAddress) {
        String methodName = "symbol";
        TypeReference<Utf8String> typeReference = new TypeReference<Utf8String>() {
        };
        Object callResult = ethCallSingleResult(web3j, methodName, typeReference, contractAddress);
        if (callResult != null) {
            return callResult.toString();
        }
        return null;
    }

    public static Integer getTokenDecimals(Web3j web3j, String contractAddress) {
        String methodName = "decimals";
        TypeReference<Uint8> typeReference = new TypeReference<Uint8>() {
        };
        Object decimals = ethCallSingleResult(web3j, methodName, typeReference, contractAddress);
        if (decimals == null) {
            return null;
        }
        return Integer.parseInt(decimals.toString());
    }

    public static BigInteger getTokenTotalSupply(Web3j web3j, String contractAddress) {
        String methodName = "totalSupply";
        TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {
        };
        Object callResult = ethCallSingleResult(web3j, methodName, typeReference, contractAddress);
        if (callResult != null) {
            return (BigInteger) callResult;
        }
        return null;
    }

    public static BigDecimal queryTokenTransactionAmountFromChain(Web3j web3j, String address) {
        try {
            for (BigInteger blockNumber = web3j.ethBlockNumber().send().getBlockNumber(); blockNumber.compareTo(BigInteger.ZERO) > 0; blockNumber = blockNumber.subtract(BigInteger.ONE)) {
                EthBlock.Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), true).send().getBlock();
                for (EthBlock.TransactionResult<EthBlock.TransactionObject> txResult : block.getTransactions()) {
                    EthBlock.TransactionObject tx = txResult.get();
                    if (tx.getTo() != null && tx.getTo().equalsIgnoreCase(address)) {
                        System.out.println("txHash" + tx.getHash());
                        System.out.println("from" + tx.getFrom());
                        System.out.println("to" + tx.getTo());
                        System.out.println("data" + tx.getValue());
                    }
                }
            }
        } catch (Exception e) {
            TokenChainService.log.warn("query token info from chain error:{}", e.getMessage());
        }
        return null;
    }
}
