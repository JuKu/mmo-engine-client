package com.jukusoft.mmo.engine.gameview.assetmanager;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
* service for asset manager
*/
public class GameAssetManager {

    //instance of asset manager
    protected AssetManager assetManager = null;

    //time limit for every asset manager update() execution
    protected int maxLoadingMillis = 10;

    protected boolean finished = false;

    //if load_assets.json contains name key, this asset is saved in this map
    protected Map<String,Object> assetsMap = new ConcurrentHashMap<>();

    //list with assets to load
    protected List<AssetInfo> loaderTasks = new ArrayList<>();

    protected List<AssetInfo> tmpList = new ArrayList<>();

    protected static final GameAssetManager instance = new GameAssetManager();

    protected GameAssetManager () {
        //create new asset manager
        this.assetManager = new AssetManager(new AbsoluteFileHandleResolver());
    }

    public static GameAssetManager getInstance() {
        return instance;
    }

    public void dispose () {
        //cleanup assets
        this.assetManager.dispose();
        this.assetManager = null;
    }

    public void update() {
        if (this.maxLoadingMillis > 0) {
            this.finished = this.assetManager.update(this.maxLoadingMillis);
        } else {
            this.finished = this.assetManager.update();
        }

        for (AssetInfo asset : this.loaderTasks) {
            if (this.assetManager.isLoaded(asset.getPath())) {
                //if asset has no unique name, it wasn't added to list

                Object asset1 = this.assetManager.get(asset.getPath(), asset.getLibGDXAssetClass());

                if (asset1 == null) {
                    throw new NullPointerException("loaded assets gets null.");
                }

                this.assetsMap.put(asset.getName(), asset1);

                //add asset to temporary list, so it could removed
                this.tmpList.add(asset);
            }
        }

        if (!this.tmpList.isEmpty()) {
            //remove loader tasks from asset list
            this.loaderTasks.removeAll(this.tmpList);

            //clear temporary list
            this.tmpList.clear();
        }
    }

    /**
    * load asset
     *
     * @param asset asset
    */
    public void load (AssetInfo asset) {
        //load asset
        this.assetManager.load(asset.getPath(), asset.getLibGDXAssetClass());

        if (asset.hasName()) {
            this.loaderTasks.add(asset);
        }
    }

    public void load (String path, Class<?> cls) {
        Objects.requireNonNull(path);

        if (path.isEmpty()) {
            throw new IllegalArgumentException("path cannot be empty.");
        }

        this.assetManager.load(path, cls);
    }

    /**
    * cleanup memory for asset
    */
    public void unload (AssetInfo asset) {
        //cleanup asset
        this.assetManager.unload(asset.getPath());

        this.loaderTasks.remove(asset);
        this.assetsMap.remove(asset.getPath());
    }

    public void unload (String path) {
        this.assetManager.unload(path);
    }

    /**
    * get instance of loaded asset
     *
     * @param fileName path to asset file
     * @param type asset type
    */
    public <T> T get (String fileName, Class<T> type) {
        if (!this.assetManager.isLoaded(fileName)) {
            throw new AssetNotLoadedException("asset '" + fileName + "' isn't loaded yet.");
        }

        return this.assetManager.get(fileName, type);
    }

    /**
    * check, if asset was loaded
     *
     * @param filePath path to asset file
    */
    public boolean isLoaded (String filePath) {
        return this.assetManager.isLoaded(filePath);
    }

    /**
     * get instance of loaded asset
     *
     * @param asset asset info
     */
    public <T> T get (AssetInfo asset) {
        if (!this.assetManager.isLoaded(asset.getPath())) {
            throw new AssetNotLoadedException("asset '" + asset.getPath() + "' isn't loaded yet.");
        }

        return this.assetManager.get(asset.getPath(), (Class<T>) asset.getLibGDXAssetClass());
    }

    /**
     * store an loaded asset to an specific name
     *
     * @param name unique asset name
     * @param asset loaded instance of asset
     */
    public <T> void addAssetByName (String name, T asset) {
        this.assetsMap.put(name, asset);
    }

    public void removeAssetName (String name) {
        this.assetsMap.remove(name);
    }

    public <T> T getAssetByName (String name, Class<T> cls) {
        Object asset = this.assetsMap.get(name);

        if (asset == null) {
            throw new GdxRuntimeException("Couldnt found asset by name: " + name + ", was this asset loaded and saved with addAssetByName() before?");
        }

        return cls.cast(asset);
    }

    public void finishLoading (String fileName) {
        this.assetManager.finishLoadingAsset(fileName);
    }

    public void finishLoading () {
        this.assetManager.finishLoading();
    }

    public float getProgress () {
        return this.assetManager.getProgress();
    }

    public Array<String> listLoadedAssets () {
        return assetManager.getAssetNames();
    }

}
