package com.jukusoft.mmo.engine.shared.utils;

import com.jukusoft.mmo.engine.shared.logger.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Utils for file operations
 *
 * Created by Justin on 24.08.2016.
 */
public class FileUtils {

    //constant strings for exceptions
    private static final String PATH_CANNOT_NULL = "path cannot be null.";
    private static final String PATH_CANNOT_EMPTY = "path cannot be empty.";

    /**
    * private constructor, so other classes cannot create an instance of FileUtils
    */
    protected FileUtils() {
        //
    }

    /**
     * read content from file
     *
     * @param path
     *            path to file
     * @param encoding
     *            file encoding
     *
     * @throws IOException if there are problems with file I/O
     *
     * @return content of file as string
     */
    public static String readFile(String path, Charset encoding) throws IOException {
        if (path == null) {
            throw new NullPointerException(PATH_CANNOT_NULL);
        }

        if (path.isEmpty()) {
            throw new IllegalArgumentException(PATH_CANNOT_EMPTY);
        }

        if (!new File(path).exists()) {
            throw new IOException("File doesnt exists: " + path);
        }

        // read bytes from file
        byte[] encoded = Files.readAllBytes(Paths.get(path.replace("/./", "/").replace("\\.\\", "\\")));

        // convert bytes to string with specific encoding and return string
        return new String(encoded, encoding);
    }

    /**
     * read lines from file
     *
     * @param path
     *            path to file
     * @param charset
     *            encoding of file
     *
     * @throws IOException if there are problems with file I/O
     *
     * @return list of lines from file
     */
    public static List<String> readLines(String path, Charset charset) throws IOException {
        if (path == null) {
            throw new NullPointerException(PATH_CANNOT_NULL);
        }

        if (path.isEmpty()) {
            throw new IllegalArgumentException(PATH_CANNOT_EMPTY);
        }

        if (!(new File(path)).exists()) {
            throw new FileNotFoundException("Couldnt find file: " + path);
        }

        return Files.readAllLines(Paths.get(path), charset);
    }

    /**
     * write text to file
     *
     * @param path
     *            path to file
     * @param content
     *            content of file
     * @param encoding
     *            file encoding
     *
     * @throws IOException if file couldn't written
     */
    public static void writeFile(String path, String content, Charset encoding) throws IOException {
        if (path == null) {
            throw new NullPointerException(PATH_CANNOT_NULL);
        }

        if (path.isEmpty()) {
            throw new IllegalArgumentException(PATH_CANNOT_EMPTY);
        }

        if (content == null) {
            throw new NullPointerException("content cannot be null.");
        }

        if (content.isEmpty()) {
            throw new IllegalArgumentException("content cannot be empty.");
        }

        if (encoding == null) {
            throw new NullPointerException("encoding cannot be null.");
        }

        Files.write(Paths.get(path), content.getBytes(encoding), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static void recursiveDeleteDirectory (File f) throws IOException {
        FileUtils.recursiveDeleteDirectory(f, true);
    }

    public static void recursiveDeleteDirectory (File f, boolean deleteDir) throws IOException {
        if (f == null) {
            throw new NullPointerException("file cannot be null.");
        }

        if (!f.exists()) {
            //we dont have to delete anything
            Logger.getAnonymousLogger().log(Level.INFO, "Dont need to delete directory, because it doesnt exists: " + f.getAbsolutePath());

            return;
        }

        //check, if it is an directory
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                recursiveDeleteDirectory(c);
            }
        }

        Log.d("FileUtils", "delete directory / file: " + f.getAbsolutePath());

        //delete directory / file
        if (deleteDir) {
            Files.delete(f.toPath());
        }
    }

    /**
    * get path to user.home directory
     *
     * @return path to user.home directory
    */
    public static String getUserHomeDir () {
        return System.getProperty("user.home");
    }

    public static String getAppHomeDir (String appName) {
        return getUserHomeDir() + "/." + appName + "/";
    }

    /**
    * removes ../ from path
     *
     * @param path path where ../ should be replaced
     *
     * @return string without ../ (resolved)
    */
    protected static String removeDoubleDotInDir(String path) {
        if (path == null) {
            throw new NullPointerException(PATH_CANNOT_NULL);
        }

        if (path.isEmpty()) {
            throw new IllegalArgumentException(PATH_CANNOT_EMPTY);
        }

        if (path.startsWith("../")) {
            throw new IllegalArgumentException("Cannot relativize paths starting with ../");
        }

        path = path.replace("\\", "/");

        if (!path.contains("/")) {
            return path;
        }

        String[] array = path.split("/");

        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < array.length; i++) {
            if (array[i].equals("..")) {
                array[i] = null;
                array[i-1] = null;
            }
        }

        for (String entry : array) {
            if (entry != null) {
                sb.append(entry + "/");
            }
        }

        return sb.toString();
    }

    /**
     * Returns the path of one File relative to another.
     *
     * @param target the target directory
     * @param base the base directory
     *
     * @see <a href="https://stackoverflow.com/questions/204784/how-to-construct-a-relative-path-in-java-from-two-absolute-paths-or-urls">Stackoverflow</a>
     *
     * @throws IOException if an error occurs while resolving the files' canonical names
     *
     * @return target's path relative to the base directory
     */
    public static File getRelativeFile(File target, File base) throws IOException {
        if (target == null) {
            throw new NullPointerException("target file cannot be null.");
        }

        if (base == null) {
            throw new NullPointerException("base file cannot be null.");
        }

        String[] baseComponents = base.getCanonicalPath().split(Pattern.quote(File.separator));
        String[] targetComponents = target.getCanonicalPath().split(Pattern.quote(File.separator));

        // skip common components
        int index = 0;

        for (; index < targetComponents.length && index < baseComponents.length; ++index) {
            if (!targetComponents[index].equals(baseComponents[index])) {
                break;
            }
        }

        StringBuilder result = new StringBuilder();

        if (index != baseComponents.length) {
            // backtrack to base directory
            for (int i = index; i < baseComponents.length; ++i) {
                result.append(".." + File.separator);
            }
        }

        for (; index < targetComponents.length; ++index) {
            result.append(targetComponents[index] + File.separator);
        }

        String targetPath = target.getPath().replace("\\", "/");

        if (!targetPath.endsWith("/")) {
            // remove final path separator
            result.delete(result.length() - File.separator.length(), result.length());
        }

        return new File(result.toString() + File.separator);
    }

    public static String getDirectoryOfFilePath (String filePath) {
        filePath = filePath.replace("\\", "/");

        if (filePath.endsWith("/")) {
            //file path is an directory
            throw new IllegalArgumentException("file path cannot be an directory path.");
        }

        String[] array = filePath.split("/");

        //directory path
        String dirPath = "";

        for (int i = 0; i < array.length - 1; i++) {
            dirPath += array[i] + "/";
        }

        return dirPath;
    }

    public static void createWritableDirIfAbsent (String dirPath) {
        File f = new File(dirPath);

        if (!f.exists()) {
            f.mkdirs();
        }

        //check, if directory is writable or try to set directory writable
        if (!f.canWrite() && !f.setWritable(true)) {
            throw new IllegalStateException("directory '" + dirPath + "' is not writable and user dont have permissions to change file permissions.");
        }
    }

}
