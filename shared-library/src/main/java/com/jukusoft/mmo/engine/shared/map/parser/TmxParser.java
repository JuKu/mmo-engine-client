package com.jukusoft.mmo.engine.shared.map.parser;

import com.jukusoft.mmo.engine.shared.map.Orientation;
import com.jukusoft.mmo.engine.shared.map.TiledMap;
import com.jukusoft.mmo.engine.shared.map.WritableTiledMap;
import org.dom4j.Element;

import java.io.File;

public class TmxParser {

    protected TmxParser () {
        //
    }

    public static TiledMap parse (Element rootElement, String tmxDir) {
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

        //TODO: add code here

        return tiledMap;
    }

}
