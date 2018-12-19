package com.jukusoft.mmo.engine.network.load;

import com.jukusoft.mmo.engine.shared.config.Cache;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.utils.HashUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
* this helper class is responsible for checking required files to detect missing or outdated files
*/
public class FileChecker {

    protected static final String LOG_TAG = "FileCheck";

    protected FileChecker () {
        //
    }

    /**
    * check required files to detect missing or outdated files
     *
     *
     * @param regionID id of region
     * @param instanceID id of instance
     * @param requiredFiles map with required files for region, key = file path (in cache), value = required file hash
    */
    public static List<String> validateFiles (long regionID, int instanceID, Map<String,String> requiredFiles) {
        List<String> invalideFiles = new ArrayList<>();

        Log.d(LOG_TAG, "validate cached region files for region " + regionID + ", instanceID: " + instanceID + "...");

        String cacheDir = Cache.getInstance().getCachePath("maps/region_" + regionID + "_" + instanceID);
        Log.d(LOG_TAG, "region cache dir: " + cacheDir);

        if (!new File(cacheDir).exists()) {
            Log.d(LOG_TAG, "create cache dir for region " + regionID + ", instanceID: " + instanceID + ": " + cacheDir);
            new File(cacheDir).mkdirs();
        }

        for (Map.Entry<String,String> entry : requiredFiles.entrySet()) {
            String filePath = entry.getKey();
            String fileHash = entry.getValue();

            Log.v(LOG_TAG, "check file " + filePath + " ...");

            String cachePath = Cache.getInstance().getCacheFilePath("maps/region_" + regionID + "_" + instanceID + "/" + filePath);

            //check, if file exists in cache
            if (!new File(cachePath).exists()) {
                Log.d(LOG_TAG, "CACHE MISS of file: " + cachePath);
                invalideFiles.add(filePath);
            } else {
                Log.d(LOG_TAG, "CACHE HIT of file: " + cachePath);

                //check file checksum
                try {
                    String checksum = HashUtils.computeMD5FileHash(new File(cachePath));

                    if (fileHash.equals(checksum)) {
                        Log.v(LOG_TAG, "cached region file is valide: " + filePath + " (checksum: " + checksum + ")");
                    } else {
                        Log.d(LOG_TAG, "OUTDATED CACHE file: " + cachePath + " (local checksum: " + checksum + ", remote checksum: " + fileHash + ")");
                        invalideFiles.add(filePath);
                    }
                } catch (Exception e) {
                    Log.w(LOG_TAG, "Exception while calculating file checksum: ", e);

                    //request file
                    invalideFiles.add(filePath);
                }
            }
        }

        Log.d(LOG_TAG, "file checker finished (" + invalideFiles.size() + " invalide cached files).");

        return invalideFiles;
    }

}
