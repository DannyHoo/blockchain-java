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
    /* 此输出的Hash */
    public String id;
    /* 此输出的新的拥有者的公钥*/
    public PublicKey reciepient;
    /* 此输出的数额 */
    public float value;
    /* 创建此输出的输出的id（解释略绕啊~） */
    public String parentTransactionId;


    public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
        this.reciepient = reciepient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(
                StringUtil.getStringFromKey(reciepient)
                        + String.valueOf(value)
                        + parentTransactionId
        );
    }

    /**
     * 判断此输出是否指向(属于)本人
     *
     * @param publicKey
     * @return
     */
    public boolean isMine(PublicKey publicKey) {
        return (publicKey == reciepient);
    }
}
