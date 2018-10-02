package com.jukusoft.mmo.engine.applayer.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class WebUtils {

    /**
    * default constructor
    */
    protected WebUtils() {
        //
    }

    /**
    * read content from website
     *
     * @param websiteURL url to website
     *
     * @link https://stackoverflow.com/questions/5867975/reading-websites-contents-into-string
     *
     * @return content of website
    */
    public static String readContentFromWebsite (String websiteURL) throws IOException {
        URL url = new URL(websiteURL);
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

        InputStream in = conn.getInputStream();
        String encoding = conn.getContentEncoding();  // ** WRONG: should use "con.getContentType()" instead but it returns something like "text/html; charset=UTF-8" so this value must be parsed to extract the actual encoding
        encoding = encoding == null ? "UTF-8" : encoding;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int len = 0;

        while ((len = in.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }

        return new String(baos.toByteArray(), encoding);
    }

}
