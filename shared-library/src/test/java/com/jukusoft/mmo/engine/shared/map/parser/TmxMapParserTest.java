package com.jukusoft.mmo.engine.shared.map.parser;

import com.jukusoft.mmo.engine.shared.map.TiledMap;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

    @Test (expected = UnsupportedOperationException.class)
    public void testParseUnsupportedRenderOrder () throws TiledParserException {
        TmxMapParser.parse(new File("../data/junit/tmx-parser/testmap-unsupported-renderorder.tmx"));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testParseMapWithoutTilesets () throws TiledParserException {
        TmxMapParser.parse(new File("../data/junit/tmx-parser/testmap-without-tilesets.tmx"));
    }

    @Test
    public void testIsSupportedVersion () {
        assertEquals(false, TmxMapParser.isSupportedVersion("0"));
        assertEquals(true, TmxMapParser.isSupportedVersion("1.0"));
        assertEquals(true, TmxMapParser.isSupportedVersion("1.1"));
        assertEquals(true, TmxMapParser.isSupportedVersion("1.2"));
        assertEquals(false, TmxMapParser.isSupportedVersion("1.3"));
        assertEquals(false, TmxMapParser.isSupportedVersion("10.0"));
    }

    @Test
    public void testParse () throws TiledParserException {
        TiledMap tiledMap = TmxMapParser.parse(new File("../data/junit/tmx-parser/testmap.tmx"));
        assertNotNull(tiledMap);
    }

}
