package com.danny.blockchain.coin;

import com.danny.blockchain.BlockChain;
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
     * 为本次交易的相关数据生成签名
     * 现实中，可能希望签名更多信息，例如使用的输出/输入/时间戳
     * 用交易发送方的秘钥来签名
     *
     * @param privateKey 交易发送方的秘钥
     */
    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) +
                StringUtil.getStringFromKey(reciepient) +
                Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    /**
     * 验证本次交易签名是否合法
     * 签名由矿工验证，只有签名验证成功后交易才能被添加到区块中去。
     * 检查区块链的有效性时，也可以检查签名
     *
     * @return
     */
    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) +
                StringUtil.getStringFromKey(reciepient) +
                Float.toString(value);
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    /**
     * 执行一些检查以确保交易有效，然后收集输入并生成输出。最后，我们抛弃了输入在我们的UTXO列表，
     * 这就意味着一个可以使用的交易输出必须之前一定是输入，所以输入的值必须被完全使用，
     * 所以付款人必须改变它们自身的金额状态。
     * @return
     */
    public boolean processTransaction() {
        if (verifySignature() == false) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }
        // 合并本次交易中所有输入项（确保他们都没有被消费过）
        for (TransactionInput i : inputs) {
            i.UTXO = BlockChain.UTXOs.get(i.transactionOutputId);
        }
        // 校验交易数额是否大于最小交易额
        if (getInputsValue() < BlockChain.minimumTransaction) {
            System.out.println("#Transaction Inputs too small: " + getInputsValue());
            return false;
        }
        // 生成交易输出
        float leftOver = getInputsValue() - value;//get value of inputs then the left over change:
        transactionId = calulateHash();
        outputs.add(new TransactionOutput(reciepient, value, transactionId));
        outputs.add(new TransactionOutput(sender, getInputsValue() - value, transactionId));

        // add outputs to unspent list
        for (TransactionOutput o : outputs) {
            BlockChain.UTXOs.put(o.id, o);
        }

        // remove transaction inputs from UTXO lists as spent:
        for (TransactionInput i : inputs) {
            if (i.UTXO == null) continue;
            BlockChain.UTXOs.remove(i.UTXO.id);
        }
        return true;
    }

    //returns sum of inputs(UTXOs) values
    public float getInputsValue() {
        float total = 0;
        for (TransactionInput i : inputs) {
            if (i.UTXO == null) continue;
            total += i.UTXO.value;
        }
        return total;
    }

    //returns sum of outputs:
    public float getOutputsValue(){
        float total=0;
        for(TransactionOutput o:outputs){
            total+=o.value;
        }
        return total;
    }
}
