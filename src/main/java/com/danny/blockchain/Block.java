package com.danny.blockchain;

import com.danny.blockchain.coin.Transaction;
import com.danny.blockchain.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author huyuyang@lxfintech.com
 * @Title: Block
 * @Copyright: Copyright (c) 2016
 * @Description: 区块链中每个区块的结构
 * @Company: lxjr.com
 * @Created on 2018-03-13 16:42:54
 */
public class Block {
    /* 当前区块hash */
    public String hash;
    /* 前一个区块的哈希*/
    public String previousHash;
    /* 区块数据 */
    //private String data;
    public String marketRoot;//计算区块hash的时候赋值
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    /* 时间戳 */
    public long timestamp;
    /* 随机数值（用于设置算力值）*/
    public long nonce;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timestamp = new Date().getTime();
        this.hash = calculateHash();
    }

    /**
     * 计算当前区块hash值
     *
     * @return
     */
    public String calculateHash() {
        String hash = StringUtil.applySha256(
                previousHash + Long.toString(timestamp) + Long.toString(nonce) + marketRoot);
        return hash;
    }

    /**
     * 算力证明
     *
     * @param difficulty
     */
    public void mineBlock(int difficulty) {
        marketRoot = StringUtil.getMarketRoot(transactions);
        String target = StringUtil.getDificultyString(difficulty);
        //System.out.println("target = " + target);
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! :" + hash + "; nonce:" + nonce);
    }

    /**
     * 向区块中添加交易
     *
     * @param transaction
     * @return
     */
    public boolean addTransaction(Transaction transaction) {
        if (transaction == null) return false;
        if (previousHash != "0") {
            if (transaction.processTransaction() != true) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }
}
