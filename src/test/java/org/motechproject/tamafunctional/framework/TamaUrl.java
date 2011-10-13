package org.motechproject.tamafunctional.framework;

import org.motechproject.tamafunctional.testdata.TestEntity;

import static org.motechproject.tamafunctional.framework.TestEnvironment.webserverName;
import static org.motechproject.tamafunctional.framework.TestEnvironment.webserverPort;

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
}
