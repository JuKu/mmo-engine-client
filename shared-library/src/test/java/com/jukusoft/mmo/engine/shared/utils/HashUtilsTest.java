package com.jukusoft.mmo.engine.shared.utils;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Created by Justin on 18.01.2017.
 */
public class HashUtilsTest {

    @Test
    public void testConstructor () {
        new HashUtils();
    }

    @Test
    public void testComputeMD5Hash () {
        String text = "text";

        //expected hash calculated with PHP md5 function (so it should be compatible with PHP, so we can use this hash method also on website).
        String expectedMD5Hash = "1cb251ec0d568de6a929b520c4aed8d1";

        String hash = HashUtils.computeMD5Hash(text);

        assertEquals(expectedMD5Hash, hash);
    }

    @Test
    public void testComputeSHAHash () {
        String text = "text";

        //expected hash calculated with PHP sha1 function (so it should be compatible with PHP, so we can use this hash method also on website).
        //String expectedMD5Hash = "372ea08cab33e71c02c651dbc83a474d32c676ea";

        String expectedMD5Hash = "Ny6gjKsz5xwCxlHbyDpHTTLGduo=";

        String hash = HashUtils.computeSHAHash(text);

        assertEquals(expectedMD5Hash, hash);
    }

    @Test
    public void testComputeMD5FileHash () throws Exception {
        //check, that hashes are always equal, also on other platforms
        assertEquals("098f6bcd4621d373cade4e832627b4f6", HashUtils.computeMD5FileHash(new File("../data/junit/hash-tests/test.txt")));
        assertEquals("ad0234829205b9033196ba818f7a872b", HashUtils.computeMD5FileHash(new File("../data/junit/hash-tests/test2.txt")));
    }

}
