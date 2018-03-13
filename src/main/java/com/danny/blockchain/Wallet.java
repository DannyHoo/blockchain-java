package com.danny.blockchain;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

/**
 * @author huyuyang@lxfintech.com
 * @Title: Wallet
 * @Copyright: Copyright (c) 2016
 * @Description: 钱包
 * @Company: lxjr.com
 * @Created on 2018-03-13 22:22:44
 */
public class Wallet {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public Wallet() {
        generateKeyPair();
    }

    /**
     * 创建公钥私钥
     */
    private void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random);   //256 bytes provides an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();
            // Set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
