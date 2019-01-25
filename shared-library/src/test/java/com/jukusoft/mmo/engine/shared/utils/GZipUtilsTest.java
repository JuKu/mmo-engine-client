package com.jukusoft.mmo.engine.shared.utils;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class GZipUtilsTest {

    @Test
    public void testConstructor () {
        new GZipUtils();
    }

    @Test
    public void testCompress () {
        byte[] data = new byte[4096];

        Random random = new Random();

        //fill data with random values
        random.nextBytes(data);

        byte[] compressedData = GZipUtils.compress(data);
        assertNotNull(compressedData);
        assertEquals(true, compressedData.length > 0);
    }

    @Test
    public void testDecompress () {
        byte[] data = new byte[4096];

        Random random = new Random();

        //fill data with random values
        random.nextBytes(data);

        byte[] compressedData = GZipUtils.compress(data);
        assertNotNull(compressedData);

        byte[] uncompressedData = GZipUtils.decompress(compressedData);
        assertNotNull(uncompressedData);
        assertEquals(data.length, uncompressedData.length);

        //check every byte in data
        for (int i = 0; i < data.length; i++) {
            assertEquals(data[i], uncompressedData[i]);
        }
    }

}
