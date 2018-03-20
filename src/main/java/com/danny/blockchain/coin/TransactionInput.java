package com.danny.blockchain.coin;

/**
 * @author huyuyang@lxfintech.com
 * @Title: TransactionInput
 * @Copyright: Copyright (c) 2016
 * @Description:
 * @Company: lxjr.com
 * @Created on 2018-03-14 17:33:04
 */
public class TransactionInput {

    /* 用于查找(上一个)相关的TransactionOutput */
    public String transactionOutputId;
    /* 引用尚未使用的transactionoutput */
    public TransactionOutput UTXO;

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
