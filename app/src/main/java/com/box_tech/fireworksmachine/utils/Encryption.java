package com.box_tech.fireworksmachine.utils;



import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.SecureRandom;

public class Encryption{
    private final static String KEY = "0253872624409878";
    private static byte[] encrypt(byte[] dataBytes, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        int blockSize = cipher.getBlockSize();

        int plaintextLength = dataBytes.length;
        if (plaintextLength % blockSize != 0) {
            plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
        }

        byte[] plaintext = new byte[plaintextLength];
        System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(plaintext);
    }

    private static byte[] decrypt(byte[] data, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        return cipher.doFinal(data);
    }

    public static String encrypt(String data, String iv)throws Exception{
        byte[] r = encrypt(data.getBytes(), iv.getBytes());
        return Base64.encodeToString(r, Base64.DEFAULT);
    }

    public static String decrypt(String data, String iv) throws Exception{
        byte[] r = Base64.decode(data, Base64.DEFAULT);
        if(r==null){
            throw new Exception("corrupted data");
        }
        else{
            r = decrypt(r, iv.getBytes());
            int i;
            for(i=r.length-1;i>0;i--){
                if(r[i] != 0){
                    break;
                }
            }
            return new String(r, 0, i+1);
        }
    }

    private static final String ivStr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom ivGen = new SecureRandom();
    public static String newIv(){
        byte[] d = new byte[16];
        ivGen.nextBytes(d);
        for(int i=0;i<d.length;i++){
            int k = (int)d[i] & 0xff;
            d[i] = (byte)ivStr.charAt(k%ivStr.length());
        }
        return new String(d);
    }
}
