package org.motechproject.tama.util;


public class FileUtil {
    public static String sanitizeFilename(String filename) {
        return filename.toLowerCase().replaceAll("[ ]*_[ ]*", "_").replaceAll("[^a-z0-9-_.]+", "_");
    }
}