package com.danny.blockchain;

import java.security.PublicKey;

/**
 * @author huyuyang@lxfintech.com
 * @Title: Transaction
 * @Copyright: Copyright (c) 2016
 * @Description: 交易类
 * @Company: lxjr.com
 * @Created on 2018-03-13 22:30:16
 */
public class Transaction {
    // this is also the hash of the transaction.
    private String transactionId;
    // senders address/public key.
    public PublicKey sender;
    // Recipients address/public key.
    public PublicKey reciepient;

}
