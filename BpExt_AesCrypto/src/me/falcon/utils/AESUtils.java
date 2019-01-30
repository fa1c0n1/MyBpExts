package me.falcon.utils;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class AESUtils {

    public static String aesEncrypt(String aesKey, String plainData, String workMode) throws Exception {
        String retStr = "";
        Key key = new SecretKeySpec(aesKey.getBytes(), "AES");
//        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        Cipher cipher = Cipher.getInstance(workMode);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherData = cipher.doFinal(plainData.getBytes());
        retStr = Hex.encodeHexString(cipherData);
        return retStr;
    }

    public static String aesDecrypt(String aesKey, String cipherDataHexStr, String workMode) throws Exception {
        String retStr = "";
        Key key = new SecretKeySpec(aesKey.getBytes(), "AES");
//        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        Cipher cipher = Cipher.getInstance(workMode);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainData = cipher.doFinal(Hex.decodeHex(cipherDataHexStr.toCharArray()));
        retStr = new String(plainData);
        return retStr;
    }

    public static String aesPadding16(String plainData) {
        StringBuffer sbuffer = new StringBuffer(plainData);
        int len = sbuffer.length();
        if (len % 16 != 0) {
           if (len > 16) {
               for (int i = 0; i < (16 - len % 16); i++) {
                   sbuffer.append(" ");
               }
           } else {
              for (int i = 0; i < (16 - len); i++) {
                  sbuffer.append(" ");
              }
           }
        }

        return sbuffer.toString();
    }
}


