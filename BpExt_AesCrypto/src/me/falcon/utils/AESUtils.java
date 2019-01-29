package me.falcon.utils;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class AESUtils {

    public static String aesEncrypt(String aesKey, String plainData) {
        return null;
    }

    public static String aesDecrypt(String aesKey, String cipherDataHexStr) {
        Key key = new SecretKeySpec(aesKey.getBytes(), "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] plainData = cipher.doFinal(Hex.decodeHex(cipherDataHexStr.toCharArray()));
            return new String(plainData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}


