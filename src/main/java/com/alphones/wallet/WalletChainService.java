package com.alphones.wallet;

import cn.hutool.core.collection.CollectionUtil;
import com.alphones.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
 * interact ethereum about wallet
 * @author Alphones
 * @date 2024/01/16 16:51
 */
@Slf4j
public class WalletChainService {


    public static BigInteger getTokenBalance(Web3j web3j, String fromAddress, String contractAddress) {
        String methodName = "balanceOf";
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = Collections.unmodifiableList(new ArrayList<>());
        Address address = new Address(fromAddress);
        inputParameters.add(address);
        TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {
        };
        outputParameters.add(typeReference);
        Function function = new Function(methodName, inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(fromAddress, contractAddress, data);
        BigInteger balanceValue = BigInteger.ZERO;
        try {
            EthCall ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            balanceValue = (BigInteger) results.get(0).getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return balanceValue;
    }

    public static BigInteger getAllowance(Web3j web3j, String owner, String spender, String contractAddress) {
        String methodName = "allowance";
        Object callInfo = null;
        String fromAddr = Config.emptyAddress;
        Function function = new Function(methodName, Arrays.asList(new Address(owner), new Address(spender)),
                Collections.singletonList(new TypeReference<Uint256>() {
                }));
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);
        EthCall ethCall = null;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            if (CollectionUtil.isEmpty(results)) {
                WalletChainService.log.error("ethCall error or has no result,method:{},reverted reason:{}", methodName, ethCall.getRevertReason());
                return null;
            }
            callInfo = results.get(0).getValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (BigInteger) callInfo;
    }
}
