package com.sawari.dev.auth;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptinAndDecryption {
    private static final String ALGO="AES";
    private static final byte[] KeyValue="1234567890123456".getBytes();
   
    public static String encryption(String password) throws Exception{
        SecretKeySpec key=new SecretKeySpec(KeyValue, ALGO);
        Cipher cipher=Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedValue=cipher.doFinal(password.getBytes());
        return Base64.getEncoder().encodeToString(encryptedValue);


    }
    public static String decrypt(String encryptedpassword)throws Exception{
        SecretKeySpec key=new SecretKeySpec(KeyValue,ALGO);
        Cipher cipher=Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedvalue=Base64.getDecoder().decode(encryptedpassword);
        byte [] decvalue=cipher.doFinal(decodedvalue);
        return new String(decvalue);
        
    }
    
}
