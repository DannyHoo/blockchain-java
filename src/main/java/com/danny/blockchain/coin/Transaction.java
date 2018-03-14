package com.danny.blockchain.coin;

import com.danny.blockchain.util.StringUtil;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

/**
 * @author huyuyang@lxfintech.com
 * @Title: Transaction
 * @Copyright: Copyright (c) 2016
 * @Description: 交易类
 * @Company: lxjr.com
 * @Created on 2018-03-13 22:30:16
 */
public class Transaction {
    /* 本次交易的hash */
    public String transactionId;
    /* 本次交易发出方（付款人）的公钥 */
    public PublicKey sender;
    /* 本次交易接收方（收款人）的公钥*/
    public PublicKey reciepient;
    /* 本次交易的数额 */
    public float value;
    /* 签名 1、用来保证只有货币的拥有者才可以用来发送自己的货币；2、用来阻止其他人试图篡改提交的交易。*/
    public byte[] signature;
    /* 输入，它是对以前的交易的引用，证明发送者有资金发送 */
    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    /* 输出，显示交易中收款方相关地址数量。(这些输出被引用为新交易的输入) */
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
    /* 随机数 防止多个相同交易产生相同Hash值*/
    private static int sequence = 0; // a rough count of how many transactions have been generated.

    /**
     * 构造函数
     *
     * @param from
     * @param to
     * @param value
     * @param inputs
     */
    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.inputs = inputs;
    }

    /**
     * 计算该次交易的Hash
     *
     * @return
     */
    private String calulateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(reciepient) +
                        Float.toString(value) + sequence
        );
    }

    /**
     * 为所有我们不希望被篡改的数据签名
     * 现实中，可能希望签名更多信息，例如使用的输出/输入/时间戳
     *
     * @param privateKey
     */
    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) +
                StringUtil.getStringFromKey(reciepient) +
                Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    /**
     * 验证签名是否合法
     * 签名将由矿工验证，只有签名验证成功后交易才能被添加到区块中去。
     * 检查区块链的有效性时，也可以检查签名
     * @return
     */
    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) +
                StringUtil.getStringFromKey(reciepient) +
                Float.toString(value);
        return StringUtil.verifyECDSASig(sender, data, signature);
    }
}
