package org.motechproject.tamafunctional.framework;

import org.motechproject.deliverytools.common.DeliveryToolsObject;

public abstract class FunctionalTestObject extends DeliveryToolsObject {
    static {
        donotInheritRootLoggerFor("org.motechproject.tamafunctional");
        donotInheritRootLoggerFor("org.motechproject.tamaperformance");
        donotInheritRootLoggerFor("org.motechproject.tamadatasetup");
        donotInheritRootLoggerFor("com.gargoylesoftware");
    }
}
