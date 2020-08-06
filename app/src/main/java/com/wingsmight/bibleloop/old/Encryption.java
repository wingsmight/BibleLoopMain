package com.wingsmight.bibleloop.old;

import android.content.Context;

import com.wingsmight.bibleloop.FileManager;

import java.io.FileOutputStream;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class Encryption
{
    public static void EncryptFile(Context context, byte[] fileBytes, String fileName, String extensionWithDot) throws Exception
    {
        FileOutputStream outputStream;
        byte[] yourKey = generateKey("password");
        byte[] enctyptBytes = encodeFile(yourKey, fileBytes);

        try {
            outputStream = context.openFileOutput( fileName + extensionWithDot, Context.MODE_PRIVATE);
            outputStream.write(enctyptBytes);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void DecryptFile(byte[] fileBytes, String filePath, String fileName, String extensionWithDot) throws Exception
    {
        byte[] yourKey = generateKey("password");
        byte[] decodedData = decodeFile(yourKey, fileBytes);
        String string = new String(decodedData);

        FileManager.BytesToFile(decodedData, filePath, fileName, extensionWithDot);
    }


    private static byte[] generateKey(String password) throws Exception
    {
        byte[] keyStart = password.getBytes("UTF-8");

        KeyGenerator kgen = KeyGenerator.getInstance("AES");

        SecureRandom secureRandom = null;
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.P)
        {
            secureRandom = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        }
        else
        {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", new CryptoProvider());
        }


        secureRandom.setSeed(keyStart);
        kgen.init(128, secureRandom);
        SecretKey skey = kgen.generateKey();
        return skey.getEncoded();
    }

    private static byte[] encodeFile(byte[] key, byte[] fileData) throws Exception
    {

        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        byte[] encrypted = cipher.doFinal(fileData);

        return encrypted;
    }

    private static byte[] decodeFile(byte[] key, byte[] fileData) throws Exception
    {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);

        byte[] decrypted = cipher.doFinal(fileData);

        return decrypted;
    }
}
