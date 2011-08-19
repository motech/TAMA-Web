package org.motechproject.tamafunctional.framework;

import org.motechproject.tamafunctional.testdata.TestEntity;

public class TamaUrl {
    public static String base() {
        return String.format("http://localhost:%s/tama/", System.getProperty("jetty.port", "8080"));
    }

    public static String baseFor(String resource) {
        return base() + resource;
    }

    public static String viewPageUrlFor(TestEntity testEntity) {
        return String.format("%s%s/%s", base(), testEntity.resourceName(), testEntity.id());
    }
}
