package org.motechproject.tama.common.util;

import java.util.UUID;

public class UUIDUtil {

    public static String newUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "");
    }
}
