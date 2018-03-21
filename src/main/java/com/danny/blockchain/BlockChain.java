package com.danny.blockchain;

import com.danny.blockchain.coin.Transaction;
import com.danny.blockchain.coin.TransactionInput;
import com.danny.blockchain.coin.TransactionOutput;
import com.danny.blockchain.coin.Wallet;
import com.danny.blockchain.util.StringUtil;
import com.google.gson.GsonBuilder;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

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
    /* 用来存放所有的输出 */
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
    /* 算力难度 */
    public static int difficuty = 5;
    /* 交易最小数额 */
    public static float minimumTransaction = 0.1f;
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction genesisTransaction;

    public static void main(String[] args) {
        //用BouncyCastleProvider作为Security的Provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        //创建钱包
        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();
        Wallet coinBase=new Wallet();

        //创建一个初始交易（给walletA发送100个币）
        genesisTransaction = new Transaction(coinBase.publicKey, walletA.publicKey,100f, null);
        //用交易发送方钱包的私钥为交易签名
        genesisTransaction.generateSignature(coinBase.privateKey);
        //手动设置初始交易的id
        genesisTransaction.transactionId="0";
        //手动添加交易输出（TransactionOutput）
        genesisTransaction.outputs.add(
                new TransactionOutput(
                        genesisTransaction.reciepient,
                        genesisTransaction.value,
                        genesisTransaction.transactionId));
        //在交易链(UTXOs)中存储初始交易
        UTXOs.put(genesisTransaction.outputs.get(0).id,genesisTransaction.outputs.get(0));

        //把初始交易添加到区块链中
        System.out.println("Creating and Mining Genesis block... ");
        Block genesis=new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        Block block1=new Block(genesis.hash);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey,40f));
        addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block2=new Block(genesis.hash);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (1000) to WalletB...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey,1000f));
        addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block3=new Block(genesis.hash);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block3.addTransaction(walletB.sendFunds(walletA.publicKey,20f));
        addBlock(block3);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

    }

    public void mineCoinTest() {
        /*long nowTime = System.currentTimeMillis();
        blockchain.add(new Block("Hi im the first block", "0"));
        System.out.println("Trying to Mine Block 1 ...");
        blockchain.get(0).mineBlock(difficuty);
        System.out.println("Mine Block 1 IS Mined! Takes Time: "+(System.currentTimeMillis()-nowTime));
        nowTime=System.currentTimeMillis();

        blockchain.add(new Block("Hi im the second block", blockchain.get(blockchain.size() - 1).hash));
        System.out.println("Trying to Mine Block 2 ...");
        blockchain.get(1).mineBlock(difficuty);
        System.out.println("Mine Block 2 IS Mined! Takes Time: "+(System.currentTimeMillis()-nowTime));
        nowTime=System.currentTimeMillis();

        blockchain.add(new Block("Hi im the thirdBlock block", blockchain.get(blockchain.size() - 1).hash));
        System.out.println("Trying to Mine Block 3 ...");
        blockchain.get(2).mineBlock(difficuty);
        System.out.println("Mine Block 3 IS Mined! Takes Time: "+(System.currentTimeMillis()-nowTime));

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println(blockchainJson);*/
    }

    /**
     * 判断当前区块是否合法
     *
     * @return
     */
    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficuty]).replace("\0", "0");
        //a temporary working list of unspent transactions at a given block state.
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

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
            if (!currentBlock.hash.substring(0, difficuty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }

            TransactionOutput tempOutput;
            //验证该区块中的每一笔交易的合法性
            for (int t = 0; t < currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);
                if (!currentTransaction.verifySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }
                for (TransactionInput input : currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);
                    if (tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }
                    if (input.UTXO.value != tempOutput.value) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }
                    tempUTXOs.remove(input.transactionOutputId); // TODO: 18/3/21
                }

                for (TransactionOutput output:currentTransaction.outputs){
                    tempUTXOs.put(output.id,output);
                }

                if (currentTransaction.outputs.get(0).reciepient!=currentTransaction.reciepient){
                    System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
                    return false;
                }

                if (currentTransaction.outputs.get(0).reciepient!= currentTransaction.sender){
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }
            }
        }
        System.out.println("Blockchain is valid");
        return true;
    }

    public static void addBlock(Block newBlock){
        newBlock.mineBlock(difficuty);
        blockchain.add(newBlock);
    }
}
