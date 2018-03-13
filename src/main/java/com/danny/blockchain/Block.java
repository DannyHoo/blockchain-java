package com.danny.blockchain;

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
    private String data;
    /* 时间戳 */
    private long timestamp;
    /* 随机数值（用于设置算力值）*/
    private int nonce;

    public Block(String data, String previousHash) {
        this.data = data;
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
                previousHash + Long.toString(timestamp) + Integer.toString(nonce) + data);
        return hash;
    }

    /**
     * 算力证明
     *
     * @param difficulty
     */
    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace("\0", "0");
        System.out.println("target = " + target);
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
            System.out.println("hash = " + hash);
            /*try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
        System.out.println("Block Mined!!! : " + hash);
    }

}
