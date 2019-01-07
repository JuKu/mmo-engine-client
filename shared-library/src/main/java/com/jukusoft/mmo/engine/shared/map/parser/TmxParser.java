package com.jukusoft.mmo.engine.shared.map.parser;

import com.jukusoft.mmo.engine.shared.map.Orientation;
import com.jukusoft.mmo.engine.shared.map.TiledMap;
import com.jukusoft.mmo.engine.shared.map.WritableTiledMap;
import org.dom4j.Element;

public class TmxParser {

    protected TmxParser () {
        //
    }

    public static TiledMap parse (Element rootElement) {
        //parse orientation
        String orientation = rootElement.attributeValue("orientation");

        WritableTiledMap tiledMap = new WritableTiledMap();

        if (orientation.equals("orthogonal")) {
            tiledMap.setOrientation(Orientation.ORTHOGONAL);
        } else if (orientation.equals("isometric")) {
            tiledMap.setOrientation(Orientation.ISOMETRIC);
        } else {
            throw new UnsupportedOperationException("orientation '" + orientation + "' isnt supported by this tmx parser.");
        }

        //TODO: add code here

        return tiledMap;
    }

}
