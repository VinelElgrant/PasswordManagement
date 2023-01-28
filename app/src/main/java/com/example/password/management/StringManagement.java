package com.example.password.management;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

import java.security.KeyFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class StringManagement {

    public static String alg = "AES/CBC/PKCS5Padding";
    private final String key = "01234567890123456789012345678901";
    private final String iv = key.substring(0, 16); // 16byte



    public String encodeString(String orig, PublicKey key) throws Exception {


        return encryptRSA(orig,key);
    }


    public String decodeString(String enco, PrivateKey key) throws Exception {
        return decryptRSA(enco, key);
    }

    public String encrypt(String text) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

        byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("string",Base64.getEncoder().encodeToString(encrypted));
            return Base64.getEncoder().encodeToString(encrypted);
        }
        else{
            return text;
        }
    }

    public String decrypt(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

        byte[] decodedBytes;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            decodedBytes = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = cipher.doFinal(decodedBytes);
            return new String(decrypted, "UTF-8");
        }
        else {
            return cipherText;
        }
    }

    public static KeyPair genRSAKeyPair() throws NoSuchAlgorithmException {

        SecureRandom secureRandom = new SecureRandom();
        KeyPairGenerator gen;
        gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(1024, secureRandom);
        KeyPair keyPair = gen.genKeyPair();
        return keyPair;
    }

    /**
     * Public Key로 RSA 암호화를 수행합니다.
     * @param plainText 암호화할 평문입니다.
     * @param publicKey 공개키 입니다.
     * @return
     */
    public static String encryptRSA(String plainText, @Nullable PublicKey publicKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bytePlain = cipher.doFinal(plainText.getBytes());
        String encrypted = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && publicKey!=null ) {
            encrypted = Base64.getEncoder().encodeToString(bytePlain);
            return encrypted;
        }
        else
            return plainText;

    }

    /**
     * Private Key로 RAS 복호화를 수행합니다.
     *
     * @param encrypted 암호화된 이진데이터를 base64 인코딩한 문자열 입니다.
     * @param privateKey 복호화를 위한 개인키 입니다.
     * @return
     * @throws Exception
     */
    public static String decryptRSA(String encrypted, @Nullable PrivateKey privateKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {

        Cipher cipher = Cipher.getInstance("RSA");
        byte[] byteEncrypted = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            byteEncrypted = Base64.getDecoder().decode(encrypted.getBytes());
        }
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] bytePlain = cipher.doFinal(byteEncrypted);
        String decrypted = new String(bytePlain, "utf-8");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && privateKey !=null)
            return decrypted;
        else
            return encrypted;
    }

    /**
     * Base64 엔코딩된 개인키 문자열로부터 PrivateKey객체를 얻는다.
     * @param keyString
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PrivateKey getPrivateKeyFromBase64String(final String keyString)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        final String privateKeyString =
                keyString.replaceAll("\\n",  "").replaceAll("-{5}[ a-zA-Z]*-{5}", "");

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PKCS8EncodedKeySpec keySpecPKCS8 =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString));
            return keyFactory.generatePrivate(keySpecPKCS8);
        }
        else
            return null;

    }

    /**
     * Base64 엔코딩된 공용키키 문자열로부터 PublicKey객체를 얻는다.
     * @param keyString
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PublicKey getPublicKeyFromBase64String(final String keyString)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        final String publicKeyString =
                keyString.replaceAll("\\n",  "").replaceAll("-{5}[ a-zA-Z]*-{5}", "");

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        X509EncodedKeySpec keySpecX509 =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString));
            return keyFactory.generatePublic(keySpecX509);
        }
        else
            return null;

    }


}
