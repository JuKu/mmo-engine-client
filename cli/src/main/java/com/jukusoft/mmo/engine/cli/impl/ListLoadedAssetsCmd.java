package com.jukusoft.mmo.engine.cli.impl;

import com.jukusoft.mmo.engine.cli.CLICommand;
import com.jukusoft.mmo.engine.gameview.assetmanager.GameAssetManager;

public class ListLoadedAssetsCmd implements CLICommand {

    @Override
    public String execute(String command, String[] args) {
        GameAssetManager assetManager = GameAssetManager.getInstance();
        StringBuilder sb = new StringBuilder();

        for (String assetName : assetManager.listLoadedAssets()) {
            sb.append(" - " + assetName + "\n");
        }

        return sb.toString();
    }

    @Override
    public String getDescription() {
        return "list all assets which are currently loaded into main memory";
    }

}
