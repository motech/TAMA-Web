package org.motechproject.tamafunctionalframework.framework;

import org.motechproject.deliverytools.common.DeliveryToolsObject;

public abstract class FunctionalTestObject extends DeliveryToolsObject {
    static {
        doNotInheritRootLoggerFor("org.motechproject.tamafunctional");
        doNotInheritRootLoggerFor("org.motechproject.tamaperformance");
        doNotInheritRootLoggerFor("org.motechproject.tamadatasetup");
        doNotInheritRootLoggerFor("com.gargoylesoftware");
    }
}
