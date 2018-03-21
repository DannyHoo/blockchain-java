package com.danny.blockchain.coin;

import com.danny.blockchain.BlockChain;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huyuyang@lxfintech.com
 * @Title: Wallet
 * @Copyright: Copyright (c) 2016
 * @Description: 钱包
 * 私钥用于签署我们不想被篡改的数据。公钥用于验证签名。
 * @Company: lxjr.com
 * @Created on 2018-03-13 22:22:44
 */
public class Wallet {

    /* 钱包私钥 */
    public PrivateKey privateKey;
    /* 钱包公钥 */
    public PublicKey publicKey;

    public Map<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();

    public Wallet() {
        generateKeyPair();
    }

    /**
     * 创建公钥私钥
     */
    private void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random);   //256 bytes provides an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();
            // Set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 计算余额
     * 把整条区块链上的UTXO依次往当前UTXOs中存储一份
     *
     * @return
     */
    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : BlockChain.UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            if (UTXO.isMine(publicKey)) {
                UTXOs.put(UTXO.id, UTXO);
                total += UTXO.value;
            }
        }
        return total;
    }

    /**
     * 创建一笔交易
     *
     * @param _recipient
     * @param value
     * @return
     */
    public Transaction sendFunds(PublicKey _recipient, float value) {
        // 判断余额是否足够
        if (getBalance() < value) {
            System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }

        // 创建输入项
        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if (total > value) break;
        }
        Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);
        newTransaction.generateSignature(privateKey);//为交易签名
        //删除UTXOs中的输入项
        for(TransactionInput input:inputs){
            UTXOs.remove(input.transactionOutputId);
        }
        return newTransaction;
    }
}
