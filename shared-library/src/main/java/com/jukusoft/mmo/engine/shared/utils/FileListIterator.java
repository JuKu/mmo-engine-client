package com.jukusoft.mmo.engine.shared.utils;

import java.io.File;

@FunctionalInterface
public interface FileListIterator {

    public void iterate (File file, String relFilePath);

}
