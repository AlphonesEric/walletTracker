package com.alphones.token.service;

import com.alphones.token.entity.Token;

import java.util.List;

public interface ITokenService {
    /*
     * save or update token info
     * @author Alphones
     * @date 2024/01/16 19:31
     * @param token
     * @return boolean
     */
    boolean saveOrUpdate(Token token);

    /*
     * delete token by address
     * @author Alphones
     * @date 2024/01/16 19:32
     * @param address
     * @return boolean
     */
    boolean deleteToken(String address);

    /*
     * query saved token config list
     * @author Alphones
     * @date 2024/01/16 19:32
     * @return java.util.List<com.alphones.token.entity.Token>
     */
    List<Token> queryTokenList();

    /*
     * query token by address
     * @author Alphones
     * @date 2024/01/16 19:32
     * @param address
     * @return com.alphones.token.entity.Token
     */
    Token queryToken(String address);
}
