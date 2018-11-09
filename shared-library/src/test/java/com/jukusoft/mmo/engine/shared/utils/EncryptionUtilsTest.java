package com.jukusoft.mmo.engine.shared.utils;

import org.junit.Test;

import java.security.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EncryptionUtilsTest {

    @Test
    public void testConstructor () {
        new EncryptionUtils();
    }

    @Test
    public void testInit () throws NoSuchAlgorithmException {
        //first, generate a key pair
        KeyPair keyPair = buildKeyPair();

        //get public and private key
        PublicKey pubKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        EncryptionUtils.pubKey = null;

        EncryptionUtils.init(pubKey);

        assertNotNull(EncryptionUtils.pubKey);
    }

    @Test
    public void testIsInitialized () throws NoSuchAlgorithmException {
        EncryptionUtils.pubKey = null;
        assertEquals(false, EncryptionUtils.isInitialized());
    }

    @Test
    public void testIsInitialized1 () throws NoSuchAlgorithmException {
        EncryptionUtils.pubKey = null;
        assertEquals(false, EncryptionUtils.isInitialized());

        //generate public key and initialize
        PublicKey publicKey = generatePublicKey();
        EncryptionUtils.init(publicKey);
        assertEquals(true, EncryptionUtils.isInitialized());

        //reset EncryptionUtils
        EncryptionUtils.pubKey = null;
    }

    @Test (expected = NullPointerException.class)
    public void testEncryptNullPublicKey () throws Exception {
        byte[] data = EncryptionUtils.encrypt(null, "test message");
    }

    @Test (expected = NullPointerException.class)
    public void testEncryptNullMessage () throws Exception {
        byte[] data = EncryptionUtils.encrypt(generatePublicKey(), null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testEncryptEmptyMessage () throws Exception {
        byte[] data = EncryptionUtils.encrypt(generatePublicKey(), "");
    }

    @Test
    public void testEncrypt () throws Exception {
        //first, generate a key pair
        KeyPair keyPair = buildKeyPair();

        //get public and private key
        PublicKey pubKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        assertEquals(true, pubKey.getEncoded().length > 0);

        byte[] data = EncryptionUtils.encrypt(pubKey, "test message");
        assertNotNull(data);
        assertEquals(true, data.length > 0);
    }

    @Test (expected = NullPointerException.class)
    public void testDecryptNullKey () throws Exception {
        EncryptionUtils.decrypt(null, new byte[2]);
    }

    @Test (expected = NullPointerException.class)
    public void testDecryptNullArray () throws Exception {
        PrivateKey privateKey = generatePrivateKey();
        EncryptionUtils.decrypt(privateKey, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testDecryptEmptyArray () throws Exception {
        PrivateKey privateKey = generatePrivateKey();
        EncryptionUtils.decrypt(privateKey, new byte[0]);
    }

    @Test (expected = IllegalStateException.class)
    public void testEncryptWithoutInit () throws Exception {
        EncryptionUtils.pubKey = null;
        EncryptionUtils.encrypt("test message");
    }

    @Test
    public void testEncryptWithInit () throws Exception {
        PublicKey publicKey = generatePublicKey();
        EncryptionUtils.init(publicKey);

        EncryptionUtils.encrypt("test message");
    }

    @Test
    public void testEncryptAndDecrypt () throws Exception {
        //first, generate a key pair
        KeyPair keyPair = buildKeyPair();

        //get public and private key
        PublicKey pubKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String message = "test message";
        byte[] encrypted = EncryptionUtils.encrypt(pubKey, message);
        String decrypted = EncryptionUtils.decrypt(privateKey, encrypted);
        assertEquals(message, decrypted);
    }

    @Test
    public void testConvertPublicKeyToBytes () throws Exception {
        //first, generate public key
        PublicKey publicKey = generatePublicKey();

        byte[] array = EncryptionUtils.convertPublicKeyToByteArray(publicKey);
        assertEquals(true, array.length > 0);

        PublicKey publicKey1 = EncryptionUtils.getPubKeyFromArray(array);
        assertEquals(publicKey, publicKey1);
    }

    @Test
    public void testGenerateKeyPair () throws NoSuchAlgorithmException {
        KeyPair keyPair = EncryptionUtils.generateKeyPair();
        assertNotNull(keyPair);
        assertNotNull(keyPair.getPublic());
        assertNotNull(keyPair.getPrivate());
    }

    protected static KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 2048;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.genKeyPair();
    }

    protected static PublicKey generatePublicKey () throws NoSuchAlgorithmException {
        //first, generate a key pair
        KeyPair keyPair = buildKeyPair();

        //get public
        PublicKey pubKey = keyPair.getPublic();

        return pubKey;
    }

    protected static PrivateKey generatePrivateKey () throws NoSuchAlgorithmException {
        //first, generate a key pair
        KeyPair keyPair = buildKeyPair();

        //get private key
        PrivateKey privateKey = keyPair.getPrivate();

        return privateKey;
    }

}
