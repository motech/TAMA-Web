package org.motechproject.tama.util;

import org.springframework.stereotype.Component;

@Component
public class FileUtil {

    public String sanitizeFilename(String filename) {
        return filename.toLowerCase().replaceAll("[^a-z0-9-_.]+", "_");
    }
}