package com.jukusoft.mmo.engine.shared.map.parser;

import com.jukusoft.mmo.engine.shared.map.Orientation;
import com.jukusoft.mmo.engine.shared.map.TiledLayer;
import com.jukusoft.mmo.engine.shared.map.TiledMap;
import com.jukusoft.mmo.engine.shared.map.tileset.TextureTileset;
import com.jukusoft.mmo.engine.shared.map.tileset.Tileset;
import com.jukusoft.mmo.engine.shared.map.WritableTiledMap;
import com.jukusoft.mmo.engine.shared.map.tileset.TsxParser;
import com.jukusoft.mmo.engine.shared.utils.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
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

        //get all layers
        List<Node> layerNodes = doc.selectNodes("/map/layer");

        if (layerNodes.isEmpty()) {
            throw new IllegalArgumentException("no layer is defined in tmx map file!");
        }

        //parse layers
        List<TiledLayer> layers = parseLayers(layerNodes);

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
                    tsxParser.load(new File(FileUtils.removeDoubleDotInDir(tmxDir + source)), firstTileID);
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
                    tileset.addImage(FileUtils.removeDoubleDotInDir(tmxDir + source), imageWidth, imageHeight, firstTileID, tilesetTileWidth, tilesetTileHeight, tileCount, columns);
                }

                //add tileset to list
                list.add(tileset);
            }
        }

        return list;
    }

    protected static List<TiledLayer> parseLayers (List<Node> layerNodes) throws TiledParserException {
        List<TiledLayer> layers = new ArrayList<>();

        for (Node layerNode : layerNodes) {
            Element layerElement = (Element) layerNode;

            //get name
            String name = layerElement.attributeValue("name");

            if (name == null) {
                throw new TiledParserException("layer name is not set.");
            }

            if (name.isEmpty()) {
                throw new TiledParserException("layer name cannot be empty.");
            }

            //get layer width & height in tiles
            int layerWidth = Integer.parseInt(layerElement.attributeValue("width"));
            int layerHeight = Integer.parseInt(layerElement.attributeValue("height"));

            float opacity = Float.parseFloat(layerElement.attributeValue("opacity", "1"));
            boolean visible = layerElement.attributeValue("visible", "1").equals("1");
            float offsetx = Float.parseFloat(layerElement.attributeValue("offsetx", "0"));
            float offsety = Float.parseFloat(layerElement.attributeValue("offsety", "0"));

            int floor = Integer.parseInt(layerElement.attributeValue("floor", "1"));
            int renderOrder = Integer.parseInt(layerElement.attributeValue("renderOrder", "1"));

            //create new layer
            TiledLayer layer = new TiledLayer(name, layerWidth, layerHeight, opacity, visible, offsetx, offsety, floor, renderOrder);

            //parse layer, get data element
            Node dataNode = layerNode.selectSingleNode("data");

            if (dataNode == null) {
                throw new TiledParserException("One of layer elements doesnt have a data node.");
            }

            Element dataElement = (Element) dataNode;

            //check encoding and compression
            String encoding = dataElement.attributeValue("encoding");

            //the compression used to compress the tile layer data. Tiled supports “gzip” and “zlib”.
            String compression = dataElement.attributeValue("compression");

            if (compression != null) {
                throw new UnsupportedOperationException("tiled map compression isnt supported yet. Compression of map: " + compression);
            }

            int[] tileIDs = null;

            if (encoding == null) {
                //no encoding set, plain XML
                tileIDs = parsePlainXMLLayer(dataElement);
            } else if (encoding.equals("base64")) {
                //base64 encoding
                tileIDs = parseBase64Layer(dataElement);
            } else {
                throw new UnsupportedOperationException("TMX layer encoding '" + encoding + "' isnt supported yet, use plain xml or 'base64' instead.");
            }

            //set tile ids
            layer.setTileIDs(tileIDs);

            //parse properties
            Node properties = layerElement.selectSingleNode("properties");

            if (properties != null) {
                //properties are specified

                List<Node> propertyNodes = properties.selectNodes("property");

                for (Node propertyNode : propertyNodes) {
                    Element propertyElement = (Element) propertyNode;

                    String key = propertyElement.attributeValue("name");
                    String type = propertyElement.attributeValue("type", "string");

                    if (key == null) {
                        throw new TiledParserException("tiled map property doesnt contains a key.");
                    }

                    switch (type) {
                        case "bool":
                            layer.addBoolProperty(key, Boolean.parseBoolean(propertyElement.attributeValue("value")));
                            break;

                        case "int":
                            layer.addIntProperty(key, Integer.parseInt(propertyElement.attributeValue("value")));
                            break;

                        case "float":
                            layer.addFloatProperty(key, Float.parseFloat(propertyElement.attributeValue("value")));
                            break;

                        case "string":
                            layer.addStringProperty(key, propertyElement.attributeValue("value"));
                            break;

                        default:
                            throw new TiledParserException("Unknown property type: " + type);
                    }
                }
            }

            //add layer to list
            layers.add(layer);
        }

        //sort render order
        Collections.sort(layers);

        return layers;
    }

    /**
     * parse data field
     *
     * @return int array with gid's (global tile IDs)
     */
    protected static int[] parsePlainXMLLayer (Element dataElement) {
        List<Node> tileIDNodes = dataElement.selectNodes("tile");

        int[] result = new int[tileIDNodes.size()];

        //convert tile nodes to int array
        for (int i = 0; i < tileIDNodes.size(); i++) {
            Element tile = (Element) tileIDNodes.get(i);
            result[i] = Integer.parseInt(tile.attributeValue("gid"));
        }

        return result;
    }

    /**
     * parse data field with base64 encoding
     *
     * @return int array with gid's (global tile IDs)
     */
    protected static int[] parseBase64Layer (Element dataElement) {
        /**
         * from tmx map format specification:
         *
         * The base64-encoded and optionally compressed layer data is somewhat more complicated to parse.
         * First you need to base64-decode it, then you may need to decompress it.
         * Now you have an array of bytes, which should be interpreted as an array of unsigned 32-bit integers using little-endian byte ordering.
         *
         * @link http://docs.mapeditor.org/en/stable/reference/tmx-map-format/#tmx-data
         */

        //https://www.baeldung.com/java-base64-encode-and-decode

        //get byte array which should be interpreted as integers
        byte[] decoded = Base64.getDecoder().decode(dataElement.getText().trim());//.decodeBase64(dataElement.getText());

        if (decoded.length % 4 != 0) {
            throw new IllegalArgumentException("invalide length of base64 string.");
        }

        IntBuffer intBuffer = ByteBuffer.wrap(decoded)
                .order(ByteOrder.LITTLE_ENDIAN)
                .asIntBuffer();

        int[] result = new int[decoded.length / 4];

        /**
         * Whatever format you choose for your layer data, you will always end up with so called “global tile IDs” (gids).
         * They are global, since they may refer to a tile from any of the tilesets used by the map.
         * In order to find out from which tileset the tile is you need to find the tileset with the highest firstgid
         * that is still lower or equal than the gid. The tilesets are always stored with increasing firstgids.
         *
         * @link http://docs.mapeditor.org/en/stable/reference/tmx-map-format/#tmx-data
         */
        for (int i = 0; i < intBuffer.limit(); i++) {
            int gid = intBuffer.get(i);

            //convert int buffer to int array
            result[i] = gid;
        }

        return result;
    }

}
