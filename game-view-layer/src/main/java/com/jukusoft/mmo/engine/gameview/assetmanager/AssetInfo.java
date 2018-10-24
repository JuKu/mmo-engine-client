package com.jukusoft.mmo.engine.gameview.assetmanager;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Data model for asset loading
 *
 * Created by Justin on 14.09.2017.
 */
public class AssetInfo {

    public enum TYPE {
        TEXTURE, TEXTURE_ATLAS, MUSIC, SOUND, UNKNOWN
    }

    protected String path = "";
    protected TYPE type = TYPE.UNKNOWN;
    protected String uniqueName = "";

    public AssetInfo(String path, TYPE type, String uniqueName) {
        if (path == null) {
            throw new NullPointerException("path cannot be null.");
        }

        if (path.isEmpty()) {
            throw new IllegalArgumentException("path cannot be empty.");
        }

        this.path = path.replace("\\", "/");
        this.path = this.path.replace("\\.\\", "\\");
        this.path = this.path.replace("/./", "/");
        this.type = type;

        this.uniqueName = uniqueName;
    }

    public AssetInfo(String path, TYPE type) {
        this(path, type, "");
    }

    public String getPath () {
        return this.path;
    }

    public TYPE getType () {
        return type;
    }

    public boolean hasName () {
        return !this.uniqueName.isEmpty();
    }

    public String getName () {
        if (this.uniqueName.isEmpty()) {
            throw new IllegalStateException("asset info doesnt contains an unique name.");
        }

        return this.uniqueName;
    }

    public Class getLibGDXAssetClass () {
        switch (this.getType()) {
            case TEXTURE:
                return Texture.class;
            case TEXTURE_ATLAS:
                return TextureAtlas.class;
            case SOUND:
                return Sound.class;
            case MUSIC:
                return Music.class;
            default:
                throw new GdxRuntimeException("Unknown libGDX asset type: " + getType() + ", path: " + path);
        }
    }

}
