package com.jukusoft.mmo.engine.applayer.version;

import com.jukusoft.mmo.engine.shared.utils.JarUtils;

import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

public class Version {

    protected String revision = "n/a";
    protected String version = "n/a";
    protected String buildJdk = "n/a";
    protected String buildTime = "n/a";
    protected String createdBy = "n/a";//default: Created-By: Apache Maven 3.3.9
    protected String vendorID = "n/a";//default: Implementation-Vendor-Id: com.jukusoft
    protected String vendor = "n/a";

    protected static Version instance = null;

    /**
    * default constructor
     *
     * @param cls class of which version is searched
    */
    public Version(Class<?> cls) {
        this.loadInfo(cls);
    }

    public Version() {
        //
    }

    protected void loadInfo (Class<?> cls) {
        //get jar file
        File file = JarUtils.getJarFileOfClass(cls);

        if (file == null) {
            //we cannot read manifest information, because class wasnt loaded from jar file
            return;
        }

        if (!file.exists()) {
            throw new IllegalStateException("JAR file doesnt exists: " + file.getAbsolutePath());
        }

        //open jar file
        try (JarFile jarFile = new JarFile(file)) {
            //get jar file attributes from jar manifest file
            final Attributes attrs = jarFile.getManifest().getMainAttributes();

            //get revision number
            this.revision = this.getOrDefault(attrs, "Implementation-Build", "n/a");

            //get version number
            this.version = this.getOrDefault(attrs, "Implementation-Version", "n/a");

            //get build jdk
            this.buildJdk = this.getOrDefault(attrs, "Build-Jdk", "n/a");

            //get build time
            this.buildTime = this.getOrDefault(attrs, "Implementation-Time", "n/a");

            //get build tool, if available
            this.createdBy = this.getOrDefault(attrs, "Created-By", "n/a");

            //get vendor id
            this.vendorID = this.getOrDefault(attrs, "Implementation-Vendor-Id", "n/a");

            //get vendor, if available
            this.vendor = this.getOrDefault(attrs, "Implementation-Vendor", "n/a");
        } catch (IOException e) {
            e.printStackTrace();
            //TODO: use logged instead, for example LocalLogger.printStacktrace(e);
        }
    }

    protected String getOrDefault (final Attributes attrs, String key, String defaultStr) {
        String value = attrs.getValue(key);

        if (value == null) {
            return defaultStr;
        }

        return value;
    }

    public String getRevision() {
        return revision;
    }

    public String getVersion() {
        return version;
    }

    public String getBuildJdk() {
        return buildJdk;
    }

    public String getBuildTime() {
        return buildTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getVendorID() {
        return vendorID;
    }

    public String getVendor() {
        return vendor;
    }

    public String getFullVersion () {
        return this.getVersion() + "-" + this.getRevision();
    }

    public static Version getInstance () {
        return instance;
    }

    public static void setInstance (Version version) {
        instance = version;
    }

}
