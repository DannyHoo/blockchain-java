package com.danny.blockchain;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;

/**
 * @author huyuyang@lxfintech.com
 * @Title: BlockChain
 * @Copyright: Copyright (c) 2016
 * @Description: 区块链
 * @Company: lxjr.com
 * @Created on 2018-03-13 17:08:26
 */
public class BlockChain {

    /* 区块链 */
    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    /* 算力难度 */
    public static int difficuty = 4;

    public static void main(String[] args) {
        blockchain.add(new Block("Hi im the first block", "0"));
        System.out.println("Trying to Mine Block 1 ...");
        blockchain.get(0).mineBlock(difficuty);

        blockchain.add(new Block("Hi im the second block", blockchain.get(blockchain.size() - 1).hash));
        System.out.println("Trying to Mine Block 2 ...");
        blockchain.get(1).mineBlock(difficuty);

        blockchain.add(new Block("Hi im the thirdBlock block", blockchain.get(blockchain.size() - 1).hash));
        System.out.println("Trying to Mine Block 3 ...");
        blockchain.get(2).mineBlock(difficuty);

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println(blockchainJson);

    }

    /**
     * 判断当前区块是否合法
     *
     * @return
     */
    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String target = new String(new char[difficuty]).replace("\0", "0");
        for (int i = 1; i < blockchain.size() - 1; i++) {
            //检查当前区块的hash是否合法
            currentBlock = blockchain.get(i);
            if (currentBlock.hash != currentBlock.calculateHash()) {
                System.out.println("Current Hashes not equal");
                return false;
            }
            //检查当前区块的previousHash是否合法
            previousBlock = blockchain.get(i - 1);
            if (currentBlock.previousHash != previousBlock.hash) {
                System.out.println("Prefious Hashes not equal");
                return false;
            }
            //检查当前区块的hash是否已经被计算出来
            if (!currentBlock.hash.substring(0, difficuty).equals(target)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
        }
        return true;
    }
}
