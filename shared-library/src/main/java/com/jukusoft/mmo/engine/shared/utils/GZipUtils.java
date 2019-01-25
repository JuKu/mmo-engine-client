package com.jukusoft.mmo.engine.shared.utils;

import com.jukusoft.mmo.engine.shared.logger.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipUtils {

    protected static final String LOG_TAG = "GZipUtils";

    /**
    * default constructor
    */
    protected GZipUtils () {
        //
    }

    public static byte[] compress (byte[] uncompressedData) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (GZIPOutputStream os = new GZIPOutputStream(baos)) {
            os.write(uncompressedData);
        } catch (IOException e) {
            Log.e(LOG_TAG, "unable to compress resource: ", e);
            throw new IllegalStateException("unable to compress resource: ", e);
        }

        return baos.toByteArray();
    }

    public static byte[] decompress (byte[] compressedData) {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (ByteArrayInputStream bis = new ByteArrayInputStream(compressedData);
             GZIPInputStream gzipInputStream = new GZIPInputStream(bis)) {

            int bytes_read;

            while ((bytes_read = gzipInputStream.read(buffer)) > 0) {
                baos.write(buffer, 0, bytes_read);
            }

            gzipInputStream.close();
            baos.close();

        } catch (IOException e) {
            Log.e(LOG_TAG, "unable to decompress resource: ", e);
            throw new IllegalStateException("unable to decompress resource: ", e);
        }

        return baos.toByteArray();
    }

}
