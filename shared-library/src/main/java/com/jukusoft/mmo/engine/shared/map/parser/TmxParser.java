package com.jukusoft.mmo.engine.shared.map.parser;

import com.jukusoft.mmo.engine.shared.map.Orientation;
import com.jukusoft.mmo.engine.shared.map.TiledMap;
import com.jukusoft.mmo.engine.shared.map.tileset.TextureTileset;
import com.jukusoft.mmo.engine.shared.map.tileset.Tileset;
import com.jukusoft.mmo.engine.shared.map.WritableTiledMap;
import com.jukusoft.mmo.engine.shared.map.tileset.TsxParser;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TmxParser {

    protected TmxParser () {
        //
    }

    public static TiledMap parse (Document doc, Element rootElement, String tmxDir) throws TiledParserException {
        //parse orientation
        String orientation = rootElement.attributeValue("orientation");

        WritableTiledMap tiledMap = new WritableTiledMap();

        //parse and set the orientation of the tiles
        if (orientation.equals("orthogonal")) {
            tiledMap.setOrientation(Orientation.ORTHOGONAL);
        } else if (orientation.equals("isometric")) {
            tiledMap.setOrientation(Orientation.ISOMETRIC);
        } else {
            throw new UnsupportedOperationException("orientation '" + orientation + "' isnt supported by this tmx parser.");
        }

        /**
         *  check render-order, only right-down is supported
         *
         * from tmx format specification:
         *
         * The order in which tiles on tile layers are rendered.
         * Valid values are right-down (the default),
         * right-up, left-down and left-up.
         *
         * In all cases, the map is drawn row-by-row.
         * (only supported for orthogonal maps at the moment)
         */
        if (!rootElement.attributeValue("renderorder").equals("right-down")) {
            throw new UnsupportedOperationException("Unsupported tmx render order '" + rootElement.attributeValue("renderorder") + "', only 'right-down' is supported!");
        }

        //get tiles count
        int width = Integer.parseInt(rootElement.attributeValue("width"));
        int height = Integer.parseInt(rootElement.attributeValue("height"));
        tiledMap.setDimension(width, height);

        //get tile width & height
        int tileWidth = Integer.parseInt(rootElement.attributeValue("tilewidth"));
        int tileHeight = Integer.parseInt(rootElement.attributeValue("tileheight"));
        tiledMap.setTileDimenstion(tileWidth, tileHeight);

        //set background color, if available
        if (rootElement.attributeValue("backgroundcolor") != null) {
            tiledMap.setBackgroundColor(rootElement.attributeValue("backgroundcolor"));
        }

        //get all tilesets
        List<Node> tilesetNodes = doc.selectNodes("/map/tileset");

        if (tilesetNodes.isEmpty()) {
            throw new IllegalArgumentException("no tileset is defined in tmx map file!");
        }

        //parse tilesets
        List<Tileset> tilesets = parseTileSets(tilesetNodes, tmxDir);
        tiledMap.setTilesets(tilesets);

        //TODO: add code here

        return tiledMap;
    }

    protected static List<Tileset> parseTileSets (List<Node> tilesetNodes, String tmxDir) throws TiledParserException {
        List<Tileset> list = new ArrayList<>();

        for (Node node : tilesetNodes) {
            Element element = (Element) node;

            //get first tileID of tileset
            int firstTileID = Integer.parseInt(element.attributeValue("firstgid"));

            if (element.attributeValue("source") != null) {
                //its a .tsx tileset

                //get source file
                String source = element.attributeValue("source");

                //parse external tileset file
                TsxParser tsxParser = new TsxParser();

                try {
                    tsxParser.load(new File(tmxDir + source), firstTileID);
                } catch (IOException e) {
                    throw new TiledParserException("IOException while loading tsx file: " + tmxDir + source, e);
                }

                list.addAll(tsxParser.listTilesets());
            } else {
                //its a normal texture tileset

                //get name
                String name = element.attributeValue("name");

                if (name == null) {
                    throw new TiledParserException("tileset doesnt contains a name.");
                }

                //get tile width
                int tilesetTileWidth = Integer.parseInt(element.attributeValue("tilewidth"));
                int tilesetTileHeight = Integer.parseInt(element.attributeValue("tileheight"));

                //get number of tiles
                int tileCount = Integer.parseInt(element.attributeValue("tilecount"));

                //get columns
                int columns = Integer.parseInt(element.attributeValue("columns"));

                //create new tileset
                TextureTileset tileset = new TextureTileset(firstTileID, name, tilesetTileWidth, tilesetTileHeight, tileCount, columns);

                //add textures to tileset
                for (Node imageNode : node.selectNodes("image")) {
                    Element imageElement = (Element) imageNode;

                    String source = imageElement.attributeValue("source");

                    if (source == null) {
                        throw new TiledParserException("tileset image source is not set.");
                    }

                    if (source.isEmpty()) {
                        throw new TiledParserException("tileset image source is empty.");
                    }

                    int imageWidth = Integer.parseInt(imageElement.attributeValue("width"));
                    int imageHeight = Integer.parseInt(imageElement.attributeValue("height"));

                    //add image to tileset
                    tileset.addImage(tmxDir + source, imageWidth, imageHeight, firstTileID, tilesetTileWidth, tilesetTileHeight, tileCount, columns);
                }

                //add tileset to list
                list.add(tileset);
            }
        }

        return list;
    }

}
