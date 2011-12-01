package org.motechproject.tamafunctional.framework;

import org.apache.log4j.Logger;

public abstract class FunctionalTestObject {
    protected Logger logger = Logger.getLogger(this.getClass());

    static {
        donotInheritRootLoggerFor("org.motechproject.tamafunctional");
        donotInheritRootLoggerFor("org.motechproject.tamaperformance");
        donotInheritRootLoggerFor("org.motechproject.tamadatasetup");
        donotInheritRootLoggerFor("org.motechproject");
        donotInheritRootLoggerFor("com.gargoylesoftware");
    }

    private static void donotInheritRootLoggerFor(String name) {
        Logger logger = Logger.getLogger(name);
        logger.setAdditivity(false);
    }

    protected void logInfo(String message, String ... params) {
        logger.info(String.format(message, params));
    }
}
