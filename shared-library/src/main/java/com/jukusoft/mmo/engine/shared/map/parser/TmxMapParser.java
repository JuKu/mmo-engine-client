package com.jukusoft.mmo.engine.shared.map.parser;

import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.map.TiledMap;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.Objects;

public class TmxMapParser {

    protected static final String LOG_TAG = "TmxParser";
    protected static final String[] SUPPORTED_TMX_VERSIONS = new String[]{
            "1.0",
            "1.1",
            "1.2"
    };

    protected TmxMapParser () {
        //
    }

    public static TiledMap parse (File tmxFile) throws TiledParserException {
        Objects.requireNonNull(tmxFile);

        if (!tmxFile.exists()) {
            throw new TiledParserException("tmx map file doesn't exists: " + tmxFile.getAbsolutePath());
        }

        Document doc = null;

        try {
            doc = getDoc(tmxFile);
        } catch (DocumentException e) {
            Log.e(LOG_TAG, "");
            throw new IllegalStateException("Cannot parse tmx file: " + tmxFile.getAbsolutePath());
        }

        //first get root element
        Element rootElement = doc.getRootElement();

        //get tmx format version
        String tmxFormatVersion = rootElement.attributeValue("version");

        if (!isSupportedVersion(tmxFormatVersion)) {
            throw new TiledParserException("Unknown tmx format version '" + tmxFormatVersion + "': " + tmxFile.getAbsolutePath());
        }

        return TmxParser.parse(rootElement);
    }

    protected static Document getDoc(File file) throws DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(file);
    }

    protected static boolean isSupportedVersion (String version) {
        for (String supportedVersion : SUPPORTED_TMX_VERSIONS) {
            if (supportedVersion.equals(version)) {
                return true;
            }
        }

        return false;
    }

}
