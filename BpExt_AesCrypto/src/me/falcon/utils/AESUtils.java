package me.falcon.utils;

<<<<<<< HEAD
=======
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

>>>>>>> 9915a671400190488d3c96a6d98bd9e1be63f7a6
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.Security;

public class AESUtils {

    public static byte[] aesEncrypt(String aesKey, byte[] plainData, String workMode) throws Exception {
        Key key = new SecretKeySpec(aesKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(workMode);
        if (workMode.contains("AES/ECB/")) {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } else if (workMode.contains("AES/CBC/")) {
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(aesKey.getBytes()));
        }
        byte[] cipherData = cipher.doFinal(plainData);
        return cipherData;
    }

    public static byte[] aesDecrypt(String aesKey, byte[] cipherData, String workMode) throws Exception {
        Key key = new SecretKeySpec(aesKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(workMode);
        if (workMode.contains("AES/ECB/")) {
            cipher.init(Cipher.DECRYPT_MODE, key);
        } else if (workMode.contains("AES/CBC/")) {
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(aesKey.getBytes()));
        }
        byte[] plainData = cipher.doFinal(cipherData);
        return plainData;
    }

<<<<<<< HEAD
    public static String aesPadding16(String plainData) throws Exception {
=======
    public static byte[] aesDecryptByBC(String aesKey, byte[] cipherData, String workMode) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        Key key = new SecretKeySpec(aesKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(workMode);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(aesKey.getBytes()));
        byte[] plainData = cipher.doFinal(cipherData);
        return plainData;
    }

    public static String aesPadding16(String plainData) {
>>>>>>> 9915a671400190488d3c96a6d98bd9e1be63f7a6
        StringBuffer sbuffer = new StringBuffer(plainData);
        byte[] tmpBytes = plainData.getBytes("utf-8");
        int len = tmpBytes.length;

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

//    public static String aesPadding16(String plainData) {
////        byte[] tmpBytes = plainData.getBytes("utf-8");
//
//        StringBuffer sbuffer = new StringBuffer(plainData);
//        int len = sbuffer.length();
//        if (len % 16 != 0) {
//           if (len > 16) {
//               for (int i = 0; i < (16 - len % 16); i++) {
//                   sbuffer.append(" ");
//               }
//           } else {
//              for (int i = 0; i < (16 - len); i++) {
//                  sbuffer.append(" ");
//              }
//           }
//        }
//
//        return sbuffer.toString();
//    }

//    public static byte[] aesDecryptByBC(String aesKey, byte[] cipherData, String workMode) throws Exception {
//        Key key = new SecretKeySpec(aesKey.getBytes(), "AES");
//        Cipher cipher = Cipher.getInstance(workMode);
//        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(aesKey.getBytes()));
//        byte[] plainData = cipher.doFinal(cipherData);
//        return plainData;
//    }
}


