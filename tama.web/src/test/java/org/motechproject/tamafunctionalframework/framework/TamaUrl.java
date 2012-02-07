package org.motechproject.tamafunctionalframework.framework;

import org.motechproject.tamafunctionalframework.testdata.TestEntity;

import static org.motechproject.tamafunctionalframework.framework.TestEnvironment.webserverName;
import static org.motechproject.tamafunctionalframework.framework.TestEnvironment.webserverPort;

public class TamaUrl {
    public static String base() {
        return String.format("http://%s:%s/tama/", webserverName(), webserverPort());
    }

    public static String baseFor(String resource) {
        return base() + resource;
    }

    public static String viewPageUrlFor(TestEntity testEntity) {
        return String.format("%s%s/%s", base(), testEntity.resourceName(), testEntity.id());
    }

    public static String ivrURL() {
        return baseFor("ivr/reply");
    }
}
