package com.danny.blockchain.coin;

import com.danny.blockchain.util.StringUtil;

import java.security.PublicKey;

/**
 * @author huyuyang@lxfintech.com
 * @Title: TransactionOutput
 * @Copyright: Copyright (c) 2016
 * @Description: 交易输出类
 * 显示从交易中发送给每一方的最终金额。这些作为新交易中的输入参考，作为证明拥有者可以发送的金额数量。
 * @Company: lxjr.com
 * @Created on 2018-03-14 17:35:00
 */
public class TransactionOutput {

    public String id;
    public PublicKey reciepient;
    public float value;
    public String parentTransactionId;

    public TransactionOutput(String id, PublicKey reciepient, float value, String parentTransactionId) {
        this.reciepient = reciepient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(
                StringUtil.getStringFromKey(reciepient)
                        + String.valueOf(value)
                        + parentTransactionId
        );
    }
}
