package com.jukusoft.mmo.engine.shared.utils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

public class EncryptionUtils {

    //https://gist.github.com/dmydlarz/32c58f537bb7e0ab9ebf

    //https://examples.javacodegeeks.com/core-java/security/get-bytes-of-a-key-pair-example/

    protected static PublicKey pubKey = null;

    protected EncryptionUtils() {
        //
    }

    /**
    * set public key
     *
     * @param pubKey RSA public key to encrypt data
    */
    public static void init (PublicKey pubKey) {
        EncryptionUtils.pubKey = pubKey;
    }

    public static boolean isInitialized () {
        return pubKey != null;
    }

    public static byte[] encrypt(PublicKey publicKey, String message) throws Exception {
        if (publicKey == null) {
            throw new NullPointerException("public key cannot be empty.");
        }

        if (message == null) {
            throw new NullPointerException("message cannot be null.");
        }

        if (message.isEmpty()) {
            throw new IllegalArgumentException("message cannot be empty.");
        }

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(message.getBytes());
    }

    public static byte[] encrypt(String message) throws Exception {
        if (pubKey == null) {
            throw new IllegalStateException("public key wasnt initialized before, call EncryptionUtils.init() before.");
        }

        return encrypt(pubKey, message);
    }

    public static byte[] decryptToBytes(PrivateKey privateKey, byte [] encrypted) throws Exception {
        if (privateKey == null) {
            throw new NullPointerException("private key cannot be empty.");
        }

        if (encrypted == null) {
            throw new NullPointerException("encrypted message array cannot be null.");
        }

        if (encrypted.length == 0) {
            throw new IllegalArgumentException("encrypted message array cannot be empty.");
        }

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(encrypted);
    }

    public static String decrypt (PrivateKey privateKey, byte [] encrypted) throws Exception {
        byte[] decrypted = decryptToBytes(privateKey, encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public static PublicKey getPubKeyFromArray (byte[] bytes) throws Exception {
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
    }

    public static byte[] convertPublicKeyToByteArray (PublicKey publicKey) {
        return publicKey.getEncoded();
    }

    public static KeyPair generateKeyPair () throws NoSuchAlgorithmException {
        final int keySize = 2048;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.genKeyPair();
    }

}
