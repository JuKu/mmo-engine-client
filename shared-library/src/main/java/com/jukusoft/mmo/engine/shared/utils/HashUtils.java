/*
* Coypright (c) 2015 Justin Kuenzel
* Apache 2.0 License
*
* This file doesnt belongs to the Pentaquin Project.
* This class is owned by Justin Kuenzel and licensed under the Apache 2.0 license.
* Many projects use this class.
*/

package com.jukusoft.mmo.engine.shared.utils;

import com.jukusoft.mmo.engine.shared.logger.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Created by Justin on 26.01.2015.
 */
public class HashUtils {

    protected static final String LOG_TAG = "HashUtils";

    protected HashUtils () {
        //
    }

    /**
    * convert byte data to hex
    */
    private static String convertToHex(byte[] data) {
        //create new instance of string buffer
        StringBuilder StringBuilder = new StringBuilder();
        String hex = "";

        //encode byte data with base64
        hex = Base64.getEncoder().encodeToString(data);
        StringBuilder.append(hex);

        //return string
        return StringBuilder.toString();
    }

    /**
    * generates SHA Hash
     *
     * @param password text
     *
     * @return hash
    */
    public static String computeSHAHash(String password) {
        MessageDigest mdSha1 = null;
        String SHAHash = "";

        try
        {
            mdSha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e1) {
            Log.e(LOG_TAG, "NoSuchAlgorithmException: ", e1);
            throw new IllegalStateException("Cannot find SHA-1 algorithm.", e1);
        }

        mdSha1.update(password.getBytes(StandardCharsets.US_ASCII));
        byte[] data = mdSha1.digest();

        SHAHash = convertToHex(data);

        return SHAHash;
    }

    /**
     * generates SHA-512 Hash for passwords
     *
     * @param password text
     *
     * @return hash
     */
    public static String computePasswordSHAHash(String password) {
        MessageDigest mdSha1 = null;
        String SHAHash = "";

        try
        {
            mdSha1 = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e1) {
            Log.e(LOG_TAG, "NoSuchAlgorithmException: ", e1);
            e1.printStackTrace();
        }

        mdSha1.update(password.getBytes(StandardCharsets.US_ASCII));
        byte[] data = mdSha1.digest();
        SHAHash = convertToHex(data);

        return SHAHash;
    }

    /**
     * generates MD5 hash
     *
     * This method is compatible to PHP 5 and Java 8.
     *
     * @param password text
     * @return hash
    */
    public static String computeMD5Hash(String password) {
        StringBuilder md5Hash = new StringBuilder();

        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(password.getBytes());
            byte[] messageDigest = digest.digest();

            for (int i = 0; i < messageDigest.length; i++)
            {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                md5Hash.append(h);
            }

        } catch (NoSuchAlgorithmException e) {
            Log.e(LOG_TAG, "NoSuchAlgorithmException: ", e);
            e.printStackTrace();
        }

        return md5Hash.toString();
    }

    /**
    * generates an MD5 file hash, like an file checksum
     *
     * @param file file
     * @return hash
     *
     * @throws Exception if hash coulnd't be generated because of missing algorithm
    */
    public static String computeMD5FileHash (File file) throws Exception {
        byte[] b = createFileChecksum(file);
        StringBuilder sb = new StringBuilder();

        for (int i=0; i < b.length; i++) {
            sb.append(Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 ));
        }

        return sb.toString();
    }

    private static byte[] createFileChecksum(File file) throws Exception {
        try (InputStream fis =  new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            MessageDigest complete = MessageDigest.getInstance("MD5");
            int numRead;

            do {
                numRead = fis.read(buffer);
                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }
            } while (numRead != -1);

            return complete.digest();
        }
    }

}
