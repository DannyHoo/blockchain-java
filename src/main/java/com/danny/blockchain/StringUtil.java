package com.danny.blockchain;

import java.security.MessageDigest;

/**
 * @author huyuyang@lxfintech.com
 * @Title: StringUtil
 * @Copyright: Copyright (c) 2016
 * @Description:
 * @Company: lxjr.com
 * @Created on 2018-03-13 16:45:40
 */
public class StringUtil {

    /**
     * 所有的输入调用此方法后均会生成一个独一无二的hash值（数字签名）
     *
     * @param input
     * @return
     */
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
