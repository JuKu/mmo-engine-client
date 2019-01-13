package com.jukusoft.mmo.engine.shared.map.tileset;

import com.jukusoft.mmo.engine.shared.map.parser.TiledParserException;
import com.jukusoft.mmo.engine.shared.utils.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TsxParser {

    //list with image textures
    protected List<TextureTileset> tilesets = new ArrayList<>();

    //external tileset format for tiled, see also http://doc.mapeditor.org/en/stable/reference/tmx-map-format/

    public TsxParser() {
        //
    }

    public void load(File tsxFile, int firstTileID) throws IOException, TiledParserException {
        if (tsxFile == null) {
            throw new NullPointerException("tsx file cannot be null.");
        }

        if (!tsxFile.exists()) {
            throw new FileNotFoundException("tsx file doesnt exists!");
        }

        String tsxDir = tsxFile.getParent().replace("\\", "/") + "/";

        Document doc = null;

        try {
            SAXReader reader = new SAXReader();
            doc = reader.read(tsxFile);
        } catch (DocumentException e) {
            throw new IllegalStateException("Cannot parse external tileset file: " + tsxFile.getAbsolutePath());
        }

        //first, get root element
        Element rootElement = doc.getRootElement();

        //get name
        String name = rootElement.attributeValue("name");
        int tileWidth = Integer.parseInt(rootElement.attributeValue("tilewidth"));
        int tileHeight = Integer.parseInt(rootElement.attributeValue("tileheight"));

        //get all tilesets
        List<Node> imageNodes = doc.selectNodes("/tileset/image");

        for (Node imageNode : imageNodes) {
            Element imageElement = (Element) imageNode;

            String source = imageElement.attributeValue("source");

            if (source == null) {
                throw new TiledParserException("no source is set for tileset '" + name + "'.");
            }

            //get width & height in pixels
            int width = Integer.parseInt(imageElement.attributeValue("width"));
            int height = Integer.parseInt(imageElement.attributeValue("height"));

            int tileCount = (width / tileWidth) * (height / tileHeight);
            int columns = (width / tileWidth);

            TextureTileset tileset = new TextureTileset(firstTileID, name, tileWidth, tileHeight, tileCount, columns);
            tileset.addImage(FileUtils.removeDoubleDotInDir(tsxDir + source), width, height, firstTileID, tileWidth, tileHeight, tileCount, columns);

            //add tileset to list
            this.tilesets.add(tileset);
        }
    }

    public List<TextureTileset> listTilesets() {
        return tilesets;
    }

}
