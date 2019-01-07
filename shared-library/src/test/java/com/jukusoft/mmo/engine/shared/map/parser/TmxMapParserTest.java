package com.jukusoft.mmo.engine.shared.map.parser;

import com.jukusoft.mmo.engine.shared.map.TiledMap;
import org.junit.Test;

import java.io.File;

public class TmxMapParserTest {

    @Test
    public void testConstructor () {
        new TmxMapParser();
    }

    @Test (expected = NullPointerException.class)
    public void testParseNullFile () throws TiledParserException {
        TmxMapParser.parse(null);
    }

    @Test (expected = TiledParserException.class)
    public void testParseNotExistentFile () throws TiledParserException {
        TmxMapParser.parse(new File("not-existent-file.txt"));
    }

    @Test (expected = IllegalStateException.class)
    public void testParseInvalideTestMap () throws TiledParserException {
        TmxMapParser.parse(new File("../data/junit/tmx-parser/invalide-testmap.tmx"));
    }

    @Test (expected = TiledParserException.class)
    public void testParseUnsupportedVersionMapFormat () throws TiledParserException {
        TmxMapParser.parse(new File("../data/junit/tmx-parser/testmap-unsupported-version.tmx"));
    }

    @Test
    public void testParse () throws TiledParserException {
        TiledMap tiledMap = TmxMapParser.parse(new File("../data/junit/tmx-parser/testmap.tmx"));
    }

}
